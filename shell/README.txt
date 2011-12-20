  
  My program consist of three functions. parseCommand(); redirect(); and main().My program starts with a while loop which prints prompt and accept user input by using sys call read() and write(). My structure in while loop: 

	1. check input, if reads 0(Ctrl + D), do exit(0);
	2. else if reads 1(only '\n'), write "invalid";
	3. else, goto parseCommand() to parse command.
  In parseCommand(), check very character in command to buffer[BUFFERSIZE] until reach space, TAB, '\n' or redirection. if space or TAB or '\n' founded. strcpy buffer to arg[]. then in parseCommand(), i realize built-in command by using sys call in hand-out chdir(), link(), unlink(). and then using fork() example in hand out for non built-in command at last free the malloc.

  In redirect(), do parse again use count to count num of < or >. no more than three. if there '>' output redirection, use fp_out = open(). if there is '<' input redirection. 



