define start 23.5000,12.0000
	call $#initFile 
	call $#mainloop 
	halt  
define looper 24.0000,173.000
	call $#hello-world 
	call $#print-string 
	print-integer r0 
	print-char $10 
	return  
define hello-world 603.000,13.5000
	push $0 
	push $10 
	push $33 
	push $100 
	push $108 
	push $114 
	push $111 
	push $119 
	push $32 
	push $111 
	push $108 
	push $108 
	push $101 
	push $72 
	return  
define print-string 25.5000,390.000
	pop r3 
	jump-zero $4 r3
	print-char r3 
	write-port-byte $#out r3
	jump $-4 
	return  
define initFile 311.000,233.000
	move $200 r1
	call $#push-filename 
	open-file-output $#out r1
	return  
define mainloop 312.000,8.50000
	add $20 
	subtract r0 $1
	call $#looper 
	jump-not-zero $-2 r0
	return  
define push-filename 310.500,423.000
	move $120 @200
	move $46 @201
	move $116 @202
	move $120 @203
	move $116 @204
	move $0 @205
	return  
