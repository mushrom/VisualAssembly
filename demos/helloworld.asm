define start 28.0000,29.0000
	add $20 
	subtract r0 $1
	call #looper 
	jump-not-zero $-2 r0
	halt  
define looper 31.0000,248.000
	call #hello-world 
	call #print-string 
	print-integer r0 
	print-char $10 
	return  
define hello-world 381.000,39.0000
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
define print-string 31.0000,465.000
	pop r3 
	jump-zero $3 r3
	print-char r3 
	jump $-3 
	return  
