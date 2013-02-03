/* 
 Implementation & bugs:
	1. Build-in command : cd, ln, rm. exit. ctrl+D to exit 
	    or start a new line. non built-in command by using execve.
	2. Idea of paring command is tracking the cmd in order, use index to track 
	    each word. when meet space or tab then reset index to 0;
	3. special cases: cd ~(~ as root which is the directory contains source file); 
	    cd \n(goes to root); rm a b ...(all a b ... files were removed); directory contains
	    space can be directed;
	4. test special cases listed in handout and all passed.
	5.use valgrind checked as many as I can, no memory leak.
	6.parsing idea: I use use char* to hold up to 20 words(char *phase[20]).
	   temp[INPUTSIZE] is for temporaly hold current word then copy to phase[numOfargs].
	   a index=0 start to keep tracking the cmd and copy to *temp. if a space or 
	   tab or \n is found then take the temp and copy to phase[numOfargs], then reset index
	   and numOfargs++
	7.should still have bugs but I tried my best to find them all.
	
	Meng Wang
*/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <sys/wait.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>

#define INPUTSIZE 1024

const char* prompt = "_cs167SHELL$ ";
char currentpath[INPUTSIZE]; //This stores current path for display.
char root[INPUTSIZE]; //This stores the root.
int status = 0;
pid_t pid;

void parseCommand(char *cmd);
void redirection(char *cmd, int len);

int main(int argc, char* args[]) {
    char *user;
    int fd;
    char command[INPUTSIZE];
    memset(root, 0, INPUTSIZE);
    getcwd(root, sizeof(root));
    
    if (argc != 1 || args[1] != NULL) {
	write(1, "usage: To initiate the program: ./sh\n", 37);
	exit(1);
    }
    
    while(1) {
	memset(command, 0, INPUTSIZE);
	memset(currentpath, 0, INPUTSIZE);
	user = getlogin();
	getcwd(currentpath, sizeof(currentpath));
	#ifndef NO_PROMPT
	if (write(1, currentpath, strlen(currentpath)) <= 0) {
	    perror(strerror(errno));
	}
	write(1, "_", 1);
	if (write(1, user, strlen(user)) <= 0 || write(1, prompt, strlen(prompt)) <= 0) {
	    perror(strerror(errno));
	}
	#endif
	if ((fd = read(0, command, INPUTSIZE)) < 0) {
	    perror(strerror(errno));
	} else if (fd == 0) {// deal with Ctrl + D without any input
	    write(1, "You also could use \"exit\" to leave the 'shell'.\n", 48);
	    exit(0);
	} else if (fd == 1) {//deal with command only has '\n'
	    //do nothing
	} else {
	    parseCommand(command);
	}
    }
    return 0;
}

void parseCommand(char* cmd) {
    char* phase[20]; //Stores command and files that sperate by space or tab
    char temp[INPUTSIZE]; //Temporary stores current words, will give to phase[i]
    int j = 0, numOfargs = 0;
    unsigned int index = 0;
    size_t i = 0, s = 0, cmd_len = strlen(cmd) - 1;
    memset(phase, 0, sizeof(char*) * 20);
    memset(temp, 0, sizeof(char) * INPUTSIZE);
    
    //handle shell has some input but end with ctrl+D. just start a new line
    if (cmd[strlen(cmd) - 1] != '\n') {
	write(1, "\n", 1);
	return;
    }
    //Line96-134 is parsing the cmd to phase[i]
    for (i = 0; i <= cmd_len; i++ ) {
	if (cmd[i] == '>' || cmd[i] == '<') {//if it there is a ">" or "<", then go to redirection method and return 
	    redirection(cmd, (int)cmd_len);
	    return;
	}
	if (cmd[i] == ' ' || cmd[i] == '\t' || cmd[i] == '\n') {
	    for (s = i + 1; s < cmd_len; s++) {//doing extra check in case of such cmd: "ls[space]>bar" 
								    //for '>' and '<' to avoid extra malloc
		if (cmd[s] == '>' || cmd[s] == '<') {
		    redirection(cmd, (int)cmd_len);
		    return;
		}
	    }
	    
	    if (index == 0) { // if a space or a tab followed by a space or tab then continue
		continue;
	    } else {
		temp[index++] = '\0';
		phase[numOfargs] = (char *)malloc(sizeof(char) * index);
		strncpy(phase[numOfargs], temp, index);
		index = 0;
		numOfargs++;
	    }
	} else {
	    if (cmd[i] == '\\') {//process the situation that directory name may have space
					  //using '\' to seperate
		if (cmd[i + 1] == '\n') {
		    write(1, "Not supported yet\n", 18);
		    return;
		} else {
		    temp[index++] = cmd[i + 1];
		    i++;
		}
	    } else {
		temp[index++] = cmd[i];
	    }
	}
    }
    //For executing 'execve', set last to NULL
    phase[numOfargs++] = NULL;

    if (strcmp(phase[0], "exit") == 0) {//exit
	for (j = 0; j < numOfargs; j++) {
	    free(phase[j]);
	}
	write(1, "ByeBye\n", 7);
	exit(3);
    } else if (strcmp(phase[0], "cd") == 0) {//cd
	if (phase[1] != NULL) {
	    if (phase[1][0] == '~') {//Deal with ~ situation. I treat source file directory as root directory 
		                                   //method is remove ~ and append the rest to root(tmp)
		char *newpath = (char *)malloc(sizeof(char) * strlen(phase[1]));
		char *tmp = (char *)malloc(sizeof(char) * strlen(root));
		char *tmp_1;
		tmp_1 = phase[1];
		strcpy(tmp, root);
		strncpy(newpath, tmp_1 + 1, strlen(tmp_1) - 1);
		strcat((char *)tmp, newpath);
		if (chdir(tmp) != 0) {//Error checking
		    switch (errno) {
			case ENOENT:
			    write(1, tmp, strlen(tmp));
			    write(1, ": No such file or directory\n", 28);
			    break;
			case EACCES: 
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": Permission denied\n", 20);
			    break;
			case EIO:
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": An I/O error occurred\n", 24);
			    break;
			case ENOTDIR:
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": Not a directory\n", 18);
			    break;
			case ENAMETOOLONG:
			    write(1, "Too long\n", 9);
			    break;
			default:
			    write(1, "ERROR: see man page\n", 20);
			    break;
		    }
		}
		free(newpath);
		free(tmp);
	    } else {
		if (chdir(phase[1]) != 0) {//Error checking
		    switch (errno) {
			case ENOENT:
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": No such file or directory\n", 28);
			    break;
			case EACCES: 
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": Permission denied\n", 20);
			    break;
			case EIO:free(phase[j]);
			    write(1, "An I/O error occurred\n", 22);
			    break;
			case ENOTDIR:
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": Not a directory\n", 18);
			    break;
			case ENAMETOOLONG:
			    write(1, "Too long\n", 9);
			    break;
			default:
			    write(1, "ERROR: see man page\n", 20);
			    break;
		    }
		}
	    }
	} else {//Deal with "cd \n" situation, just change to root.
	    chdir(root);
	}
    } else if (strcmp(phase[0], "rm") == 0) {//rm
	int numargs = 1;
	if (numOfargs == 2 || numOfargs == 3) {//if the command is "rm a\n" or "rm \n" 
	    if (phase[1] != NULL) {
		if (unlink(phase[1]) != 0) {//Error checking
		    switch (errno) {
			case EACCES: 
			    write(1, phase[1], strlen(phase[1]));
			    write(1, ": Permission denied\n", 20);
			    break;
			case EISDIR:
			    write(1, phase[0], strlen(phase[0]));
			    write(1, ": cannot remove '", 17);
			    write(1, phase[1], strlen(phase[1]));
			    write(1, "': Is a directory\n", 18);
			    break;
			case ENOENT:
			    write(1, phase[0], strlen(phase[0]));
			    write(1, ": cannot remove '", 17);
			    write(1, phase[1], strlen(phase[1]));
			    write(1, "': No such file or directory\n", 29);
			    break;
			default:
			    write(1, "ERROR: see man page\n", 20);
			    break;
		    }
		}
	    } else {
		write(1, phase[0], strlen(phase[0]));
		write(1, ": missing operand\n", 18);
	    }
	} else {
	    for (; numargs < numOfargs - 1; numargs++) {//if there are multiple files are going to be removed
		if (phase[numargs] != NULL) {
		    if (unlink(phase[numargs]) != 0) {//Error checking
			switch (errno) {
			    case EACCES: 
				write(1, phase[numargs], strlen(phase[numargs]));
				write(1, ": Permission denied\n", 20);
				break;
			    case EISDIR:
				write(1, phase[0], strlen(phase[0]));
				write(1, ": cannot remove '", 17);
				write(1, phase[numargs], strlen(phase[numargs]));
				write(1, "': Is a directory\n", 18);
				break;
			    case ENOENT:
				write(1, phase[0], strlen(phase[0]));
				write(1, ": cannot remove '", 17);
				write(1, phase[numargs], strlen(phase[numargs]));
				write(1, "': No such file or directory\n", 29);
				break;
			    default:
				write(1, "ERROR: see man page\n", 20);
				break;
			}
		    }
		}
	    }
	}
    } else if (strcmp(phase[0], "ln") == 0) {//ln
	if (phase[1] != NULL && phase[2] != NULL) {//Error checking
	    if (link(phase[1], phase[2]) != 0) {
		switch (errno) {
		    case EACCES:
			write(1, phase[2], strlen(phase[2]));
			write(1, ": access denied\n", 16);
			break;
		    case EEXIST:
			write(1, phase[2], strlen(phase[2]));
			write(1, ": path already exist\n", 21);
			break;
		    case EIO:
			write(1, "I/O error occurred\n", 19);
			break;
		    case EPERM:
			write(1, phase[1], strlen(phase[1]));
			write(1, ": is a directory\n", 17);
			break;
		    case EROFS:
			write(1, "The file is on a read-only file system\n", 39);
			break;
		    case ENOENT:
			write(1, phase[1], strlen(phase[1]));
			write(1, " or ", 4);
			write(1, phase[2], strlen(phase[2]));
			write(1, "directory component does not exist or is a dangling symbolic link\n", 66);
			break;
		    default:
			write(1, "ERROR: see man page\n", 20);
			break;
		}
	    }
	} else {
	    write(1, phase[0], strlen(phase[0]));
	    write(1, ": missing file operand\n", 23);
	}
    } else {//Here is going to process non built-in command
	pid = fork();
	if (pid < 0) {
	    perror("fork");
	    for (j = 0; j < numOfargs; j++) {
		free(phase[j]);
	    }
	    exit(1);
	} else if (pid == 0) {
	    char *envp[] = {NULL};
	    char c[30] = "/bin/";
	    strcat(c, phase[0]);
	    execve(c, phase, envp);
	    if (errno == ENOENT) {
		fprintf(stderr, "sh: command not found %s\n", phase[0]);
		for (j = 0; j < numOfargs; j++) {
		    free(phase[j]);
		}
		exit(1);
	    } else {
		fprintf(stderr, "sh: execution of %s failed: %s\n", phase[0], strerror(errno));
		for (j = 0; j < numOfargs; j++) {
		    free(phase[j]);
		}
		exit(1);
	    }
	} else {
	    waitpid(pid, &status, 0);
	}
    }
    for (j = 0; j < numOfargs; j++) {
	free(phase[j]);
    }
}

void redirection(char *cmd, int len) {
    char *phase[20];
    char *filename[5];
    char temp[INPUTSIZE];
    int fd_in, fd_out;
    int flag_in = -1, flag_out = -1;
    int i, j, k = 0, q = 0, numOfargs = 0, numOfredirctor = 0;
    int append = O_TRUNC; 
    unsigned int index = 0;
    memset(phase, 0, sizeof(char*) * 20);
    memset(filename, 0, sizeof(char*) * 5);
    memset(temp, 0, sizeof(char) * INPUTSIZE);

    for (i = 0; i <= len; i++) {
	if (cmd[i] == ' ' || cmd[i] == '	' || cmd[i] == '\n' || cmd[i] == '>' || cmd[i] == '<') {
	    if (cmd[i] == '>' || cmd[i] == '<') {
		numOfredirctor++;//count then num of redirector
		if (numOfredirctor >= 3) {//should not have 3 or more redirectors
		    write(1, "ERROR, cannot have 3 or more redirector\n", 40);
		    return;
		}
		if (cmd[i] == '<') {
		    flag_in = numOfredirctor - 1;
		    for (j = i + 1; j < len; j++) {
			if (cmd[j] == '<') {
			    write(1, "ERROR - can't have 2 input redirects on one line\n", 49);
			    return;
			}
		    }
		} else {
		    flag_out = numOfredirctor - 1;
		    if (cmd[i - 1] == '>') {//here is processing '>>'
			flag_out--;
			append = O_APPEND;
		    }
		}
		
		if (numOfredirctor == 1 && index > 0) {//no space in cmd
		    temp[index++] = '\0';
		    phase[numOfargs] = (char *)malloc(sizeof(char) * index);
		    strcpy(phase[numOfargs], temp);
		    index = 0;
		    numOfargs++;
		} else if (numOfredirctor == 1 && index == 0 && cmd[i] == '>' && phase[0] == NULL) {
		    write(1, "ERROR - No command\n", 19);
		    return;
		}
	    }
	    if (index == 0) {
		continue;
	    } else {
		temp[index++] = '\0';
		if (numOfredirctor == 0) {
		    phase[numOfargs] = (char *)malloc(sizeof(char) * index);
		    strncpy(phase[numOfargs], temp, (size_t)index);
		    index = 0;
		    numOfargs++;
		} else {
		    filename[k] = (char *)malloc(sizeof(char) * index);
		    strcpy(filename[k], temp);
		    index = 0;
		    k++;
		}
	    }
	} else {
	    temp[index++] = cmd[i];
	}
    }
    phase[numOfargs++] = (char *)0;
    
    pid = fork();
    if (pid < 0) {
	perror("fork");
	for (q = 0; q < numOfargs; q++) {
	    free(phase[q]);
	}
	exit(1);
    } else if (pid == 0) {
	char *envp[] = {NULL};
	char p[30] = "/bin/";
	strcat(p, phase[0]);
	if (flag_out != -1) {
	    if ((fd_out = open(filename[flag_out], O_WRONLY|O_CREAT|append, S_IRUSR|S_IWUSR)) == -1) {
		switch (errno) {
		    case EACCES:
			write(1, filename[flag_out], strlen(filename[flag_out]));
			write(1, ": access denied\n", 16);
			break;
		    case EEXIST:
			write(1, filename[flag_out], strlen(filename[flag_out]));
			write(1, ": path already exist\n", 21);
			break;
		    case EISDIR:
			write(1, filename[flag_out], strlen(filename[flag_out]));
			write(1, "': Is a directory\n", 18);
			break;
		    case ENOENT:
			write(1, filename[flag_out], strlen(filename[flag_out]));
			write(1, ": No such file or directory\n", 28);
		    default:
			write(1, "ERROR: see man page\n", 20);
			break;
		}
		return;
	    }
	    if (dup2(fd_out, STDOUT_FILENO) == -1) {
		for (q = 0; q < numOfargs; q++) {
		    free(phase[q]);
		}
		exit(1);
	    }
	}
	if (flag_in != -1) {
	    if ((fd_in = open(filename[flag_in], O_RDONLY, S_IRUSR|S_IWUSR)) == -1) {
		switch (errno) {
		   case EACCES:
			write(1, filename[flag_in], strlen(filename[flag_in]));
			write(1, ": access denied\n", 16);
			break;
		    case EEXIST:
			write(1, filename[flag_in], strlen(filename[flag_in]));
			write(1, ": path already exist\n", 21);
			break;
		    case EISDIR:
			write(1, filename[flag_in], strlen(filename[flag_in]));
			write(1, "': Is a directory\n", 18);
			break;
		    case ENOENT:
			write(1, filename[flag_in], strlen(filename[flag_in]));
			write(1, ": No such file or directory\n", 28);
			break;
		    default:
			write(1, "ERROR: see man page\n", 20);
			break; 
		}
	    }
	    if (dup2(fd_in, STDIN_FILENO) == -1) {
		for (q = 0; q < numOfargs; q++) {
		    free(phase[q]);
		}
		exit(1);
	    }
	}
	execve(p, phase, envp);
    } else {
	waitpid(pid, &status, 0);
    }
    for (q = 0; q < numOfargs; q++) {
	free(phase[q]);
    }
    if (flag_in != -1) {
        free(filename[flag_in]);
    }
    if (flag_out != -1) {
        free(filename[flag_out]);
    }
}