.data
_globalVar1_: .word 0
_globalPi_: .float 3.1415
_isEnabled_: .word 0
_counter_: .word 10
_msgCounter_: .word 0
_sum_: .word _addNumbers_(...)
_flag_: .word 0
_msgFlagTrue_: .word 0
_msgFlagFalse_: .word 0

.text
main:
# Reserva para variable _globalVar1_
# Reserva para variable _globalPi_
# Cargar literal float 3.1415
li.s $f0, 3.1415
mov.s $f2, $f0
mfc1 $t2, $f2
# Asignación a variable _globalPi_
s.s $f0, _globalPi_
# Reserva para variable _isEnabled_
# Cargar literal booleano true
li $t2, 1
# Asignación a variable _isEnabled_
sw $t2, _isEnabled_
_printMessage_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Acceso a variable _msg_
lw $t2, _msg_
la $a0, _msg_
li $v0, 4
syscall
# Cargar literal entero 0
li $t2, 0
li $v0, 0
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
_addNumbers_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Reserva para variable _result_
# Acceso a variable _x_
lw $t2, _x_
# Acceso a variable _y_
lw $t2, _y_
# Evaluación de +
lw $t0, _x_
lw $t1, _y_
add $t2, $t0, $t1
# Asignación a variable _result_
sw $t2, _result_
# Acceso a variable _result_
lw $t2, _result_
move $v0, _result_
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
_multiplyNumbers_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Reserva para variable _product_
# Acceso a variable _x_
lw $t2, _x_
# Acceso a variable _y_
lw $t2, _y_
# Evaluación de *
lw $t0, _x_
lw $t1, _y_
mul $t2, $t0, $t1
# Asignación a variable _product_
sw $t2, _product_
# Acceso a variable _product_
lw $t2, _product_
move $v0, _product_
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
# Reserva para variable _counter_
# Cargar literal entero 10
li $t2, 10
# Asignación a variable _counter_
sw $t2, _counter_
# Reserva para variable _msgCounter_
# Acceso a literal string Contador inicializado.
# Asignación a variable _msgCounter_
sw $t2, _msgCounter_
# Acceso a variable _msgCounter_
lw $t2, _msgCounter_
lw $a0, _msgCounter_
li $v0, 1
syscall
# Reserva para variable _sum_
# Cargar literal entero 5
li $t2, 5
# Cargar literal entero 7
li $t2, 7
addi $sp, $sp, -4
sw 7, 0($sp)
addi $sp, $sp, -4
sw 5, 0($sp)
jal _addNumbers_
move $t2, $v0
addi $sp, $sp, 8
# Asignación a variable _sum_
sw $t2, _sum_
# Acceso a variable _sum_
lw $t2, _sum_
lw $a0, _sum_
li $v0, 1
syscall
# Reserva para variable _flag_
# Cargar literal booleano true
li $t2, 1
# Asignación a variable _flag_
sw $t2, _flag_
# Reserva para variable _msgFlagTrue_
# Acceso a literal string Flag está activado.
# Asignación a variable _msgFlagTrue_
sw $t2, _msgFlagTrue_
# Reserva para variable _msgFlagFalse_
# Acceso a literal string Flag está desactivado.
# Asignación a variable _msgFlagFalse_
sw $t2, _msgFlagFalse_
# Acceso a variable _flag_
lw $t2, _flag_
# Acceso a variable _msgFlagTrue_
lw $t2, _msgFlagTrue_
lw $a0, _msgFlagTrue_
li $v0, 1
syscall
# Inicio de bloque
# Fin de bloque
# Acceso a variable _msgFlagFalse_
lw $t2, _msgFlagFalse_
lw $a0, _msgFlagFalse_
li $v0, 1
syscall
# Inicio de bloque
# Fin de bloque
# Inicio de if-else
lw $t2, _flag_
beqz $t2, if_false_0
j if_exit_0
if_false_0:
if_exit_0:
# Cargar literal entero 0
li $t2, 0
li $v0, 0
j exit_function
# Inicio de bloque
# Fin de bloque

# Fin del programa - syscall de salida
li $v0, 10
syscall
