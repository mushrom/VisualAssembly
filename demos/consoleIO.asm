define start 18.5000,25.5000
	call $#print-prompt 
	call $#wait-for-input 
	call $#echo 
	jump $-3 
define wait-for-input 20.5000,213.500
	port-has-available $#console 
	jump-zero $-1 r0
	return  
define echo 20.5000,374.500
	port-has-available $#console 
	jump-zero $4 r0
	read-port-byte $#console 
	print-char r0 
	jump $-4 
	return  
define print-prompt 308.000,24.5000
	print-char $10 
	print-char $62 
	print-char $32 
	return  
