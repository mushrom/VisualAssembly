define start 26.5000,15.0000
	call $#connect-socket 
	close-port $#sock 
	halt  
define load-hostname 31.5000,167.500
	move $101 @200
	move $120 @201
	move $97 @202
	move $109 @203
	move $112 @204
	move $108 @205
	move $101 @206
	move $46 @207
	move $99 @208
	move $111 @209
	move $109 @210
	move $58 @211
	move $56 @212
	move $48 @213
	move $0 @214
	return  
define connect-socket 332.000,17.5000
	call $#load-hostname 
	open-client-socket $#sock $200
	call $#load-request 
	move $301 rsp
	call $#send-request 
	call $#read-response 
	return  
define load-request 629.000,270.500
	move $71 @300
	move $69 @301
	move $84 @302
	move $32 @303
	move $47 @304
	move $32 @305
	move $72 @306
	move $84 @307
	move $84 @308
	move $80 @309
	move $47 @310
	move $49 @311
	move $46 @312
	move $49 @313
	move $13 @314
	move $10 @315
	move $72 @316
	move $111 @317
	move $115 @318
	move $116 @319
	move $58 @320
	move $32 @321
	move $101 @322
	move $120 @323
	move $97 @324
	move $109 @325
	move $112 @326
	move $108 @327
	move $101 @328
	move $46 @329
	move $99 @330
	move $111 @331
	move $109 @332
	move $13 @333
	move $10 @334
	move $13 @335
	move $10 @336
	move $0 @337
	return  
define send-request 330.000,309.500
	move rsp r7
	pop r1 
	jump-zero $5 r1
	write-port-byte $#sock r1
	add $2 rsp
	move r0 rsp
	jump $-5 
	return  
define read-response 630.000,19.0000
	port-has-available $#sock 
	jump-zero $4 r0
	read-port-byte $#sock r0
	print-char r0 
	jump $-4 
	return  
