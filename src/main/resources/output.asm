.data
_fl1_: .float 0.0
_in1_: .word 0
_fl2_: .float 0.0
_bl0_: .word 0
_bl1_: .word 0

.text
.globl main
main:
_func1_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Reserva para variable _h_
# Cargar literal float 0.01
li.s $f0, 0.01
mov.s $f2, $f0
mfc1 $t2, $f2
# Asignación a variable _h_
s.s $f0, _h_
# Reserva para variable _x22_
# Cargar literal char a
li $t2, 97
# Asignación a variable _x22_
sw $t2, _x22_
# Reserva para variable _miChar_
# Cargar literal char !
li $t2, 33
# Asignación a variable _miChar_
sw $t2, _miChar_
# Reserva para variable _miChar2_
# Cargar literal char !
li $t2, 33
# Asignación a variable _miChar2_
sw $t2, _miChar2_
# Reserva para variable _x30_
# Cargar literal entero 1
li $t2, 1
# Asignación a variable _x30_
sw $t2, _x30_
# Reserva para variable _x40_
# Cargar literal booleano false
li $t2, 0
# Asignación a variable _x40_
sw $t2, _x40_
# Reserva para variable _x50_
# Cargar literal entero 1000
li $t2, 1000
# Reserva para array _x50_ de tamaño 1000
# Reserva para variable _x50_
# Acceso a literal string Hola a todos los que est abreempaque a cierraempaque  haciendo un compilador nuevo\n
# Asignación a variable _x50_
sw $t2, _x50_
# Acceso a variable _x22_
lw $t2, _x22_
# Cargar literal entero 45
li $t2, 45
# Acceso a variable _var_
lw $t2, _var_
# Evaluación de &&
lw $t0, 45
lw $t1, _var_
and $t2, $t0, $t1
# Evaluación de <=
lw $t0, _x22_
lw $t1, (45 && _var_)
slt $t3, $t0, $t1
xori $t2, $t3, 1
# Cargar literal float 5.6
li.s $f0, 5.6
mov.s $f2, $f0
mfc1 $t2, $f2
# Evaluación de >
lw $t0, (_x22_ <= (45 && _var_))
lw $t1, 5.6
slt $t2, $t1, $t0
# Reserva para variable _y_
# Cargar literal entero 10
li $t2, 10
# Asignación a variable _x22_
sw $t2, _x22_
# Reserva para variable _ch33_
# Cargar literal char a
li $t2, 97
# Asignación a variable _ch33_
sw $t2, _ch33_
# Inicio de bloque
# Fin de bloque
# Reserva para variable _y_
# Reserva para variable _str2_
# Acceso a literal string sdff
# Asignación a variable _str2_
sw $t2, _str2_
# Inicio de bloque
# Fin de bloque
# Inicio de if-else
lw $t2, ((_x22_ <= (45 && _var_)) > 5.6)
beqz $t2, if_false_0
j if_exit_0
if_false_0:
if_exit_0:
# Cargar literal entero 0
li $t2, 0
# Asignación a variable _i_
sw $t2, _i_
# Acceso a variable _i_
lw $t2, _i_
# Cargar literal entero 4
li $t2, 4
# Evaluación de <
lw $t0, _i_
lw $t1, 4
slt $t2, $t0, $t1
# Acceso a variable _j_
lw $t2, _j_
# Evaluación de +
lw $t0, (_i_ < 4)
lw $t1, _j_
add $t2, $t0, $t1
# Post-incremento de _i_
lw $t0, _i_
move $t2, $t0
addi $t0, $t0, 1
sw $t0, _i_
# Acceso a variable _i_
lw $t2, _i_
la $a0, _i_
li $v0, 4
syscall
# Inicio de bloque
# Fin de bloque
# Inicio del for: inicialización completada
for_start_1:
lw $t2, ((_i_ < 4) + _j_)
beqz $t2, for_exit_2
for_step_1:
# Ejecución de la fase step del for
j for_start_1
for_exit_2:
# Acceso a literal string Hola mundo
la $a0, "Hola mundo"
li $v0, 4
syscall
li $v0, 5
syscall
sw $v0, _x22_
# Cargar literal float 5.6
li.s $f0, 5.6
mov.s $f2, $f0
mfc1 $t2, $f2
move $v0, 5.6
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
_func2_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Reserva para variable _h_
# Cargar literal entero 1
li $t2, 1
# Cargar literal entero 1
li $t2, 1
# Evaluación de +
lw $t0, 1
lw $t1, 1
add $t2, $t0, $t1
move $v0, (1 + 1)
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
_func3_:
addi $sp, $sp, -8
sw $ra, 4($sp)
sw $fp, 0($sp)
move $fp, $sp
# Reserva para variable _b1_
# Acceso a variable _b1_
lw $t2, _b1_
move $v0, _b1_
j exit_function
# Inicio de bloque
# Fin de bloque
move $sp, $fp
lw $fp, 0($sp)
lw $ra, 4($sp)
addi $sp, $sp, 8
jr $ra
# Reserva para variable _fl1_
# Reserva para variable _fl1_
# Cargar literal float 56.6
li.s $f0, 56.6
mov.s $f2, $f0
mfc1 $t2, $f2
# Asignación a variable _fl1_
s.s $f0, _fl1_
# Reserva para variable _in1_
# Post-decremento de _fl1_
lw $t0, _fl1_
move $t2, $t0
addi $t0, $t0, -1
sw $t0, _fl1_
# Cargar literal entero 14
li $t2, 14
# Evaluación de - (binario)
lw $t0, _fl1_--
lw $t1, 14
sub $t2, $t0, $t1
# Post-incremento de _in1_
lw $t0, _in1_
move $t2, $t0
addi $t0, $t0, 1
sw $t0, _in1_
# Cargar literal entero 7
li $t2, 7
# Evaluación de +
lw $t0, _in1_++
lw $t1, 7
add $t2, $t0, $t1
# Cargar literal entero 15
li $t2, 15
# Evaluación de - (binario)
lw $t0, (_in1_++ + 7)
lw $t1, 15
sub $t2, $t0, $t1
# Evaluación de /
lw $t0, (_fl1_-- - 14)
lw $t1, ((_in1_++ + 7) - 15)
div $t0, $t1
mflo $t2
# Asignación a variable _in1_
sw $t2, _in1_
# Reserva para variable _fl2_
# Cargar literal float 3.7
li.s $f0, 3.7
mov.s $f2, $f0
mfc1 $t2, $f2
# Acceso a variable _fl1_
lw $t2, _fl1_
# Cargar literal float 45.6
li.s $f0, 45.6
mov.s $f2, $f0
mfc1 $t2, $f2
# Cargar literal entero 76
li $t2, 76
# Evaluación de %
lw $t0, 45.6
lw $t1, 76
div $t0, $t1
mfhi $t2
# Evaluación de +
lw $t0, _fl1_
lw $t1, ((45.6 % 76))
add $t2, $t0, $t1
# Evaluación de ^ (potencia)
lw $t0, 3.7
lw $t1, (_fl1_ + ((45.6 % 76)))
li $t2, 1
pow_loop_3:
beqz $t1, pow_exit_3
mul $t2, $t2, $t0
addi $t1, $t1, -1
j pow_loop_3
pow_exit_3:
# Asignación a variable _fl2_
s.s $f0, _fl2_
# Cargar literal entero 10
li $t2, 10
# Cargar literal entero 67
li $t2, 67
# Acceso a variable _i_
lw $t2, _i_
# Evaluación de +
lw $t0, 67
lw $t1, _i_
add $t2, $t0, $t1
# Acceso a elemento de array _arr_
move $t3, $t2
la $t0, _arr_
mul $t3, $t3, 4
add $t0, $t0, $t3
lw $t2, 0($t0)
# Evaluación de - (binario)
lw $t0, 10
lw $t1, _arr_[(67 + _i_)]
sub $t2, $t0, $t1
# Acceso a variable _hola_
lw $t2, _hola_
# Cargar literal booleano true
li $t2, 1
# Acceso a literal string hola mundo
# Cargar literal float 4.5
li.s $f0, 4.5
mov.s $f2, $f0
mfc1 $t2, $f2
# Cargar literal char a
li $t2, 97
addi $sp, $sp, -4
sw 'a', 0($sp)
addi $sp, $sp, -4
sw 4.5, 0($sp)
addi $sp, $sp, -4
sw "hola mundo", 0($sp)
addi $sp, $sp, -4
sw true, 0($sp)
addi $sp, $sp, -4
sw _hola_, 0($sp)
jal _func1_
move $t2, $v0
addi $sp, $sp, 20
# Evaluación de *
lw $t0, (10 - _arr_[(67 + _i_)])
lw $t1, _func1_(...)
mul $t2, $t0, $t1
# Asignación a variable _arr_
sw $t2, _arr_
# Cargar literal float 4.5
li.s $f0, 4.5
mov.s $f2, $f0
mfc1 $t2, $f2
# Acceso a variable _miChar_
lw $t2, _miChar_
# Evaluación de %
lw $t0, 4.5
lw $t1, _miChar_
div $t0, $t1
mfhi $t2
# Cargar literal float 0.005
li.s $f0, 0.005
mov.s $f2, $f0
mfc1 $t2, $f2
# Evaluación de ^ (potencia)
lw $t0, (4.5 % _miChar_)
lw $t1, 0.005
li $t2, 1
pow_loop_4:
beqz $t1, pow_exit_4
mul $t2, $t2, $t0
addi $t1, $t1, -1
j pow_loop_4
pow_exit_4:
# Asignación a variable _fl1_
sw $t2, _fl1_
# Reserva para variable _bl0_
# Cargar literal float 6.7
li.s $f0, 6.7
mov.s $f2, $f0
mfc1 $t2, $f2
# Cargar literal float 8.9
li.s $f0, 8.9
mov.s $f2, $f0
mfc1 $t2, $f2
# Evaluación de !=
lw $t0, 6.7
lw $t1, 8.9
sne $t2, $t0, $t1
# Asignación a variable _bl0_
sw $t2, _bl0_
# Reserva para variable _bl1_
# Acceso a variable _in1_
lw $t2, _in1_
# Acceso a variable _fl1_
lw $t2, _fl1_
# Cargar literal booleano false
li $t2, 0
# Evaluación de ||
lw $t0, _fl1_
lw $t1, false
or $t2, $t0, $t1
# Cargar literal entero 3
li $t2, 3
# Acceso a variable _in1_
lw $t2, _in1_
addi $sp, $sp, -4
sw _in1_, 0($sp)
addi $sp, $sp, -4
sw 3, 0($sp)
jal _func2_
move $t2, $v0
addi $sp, $sp, 8
# Cargar literal entero 56
li $t2, 56
# Evaluación de >
lw $t0, _func2_(...)
lw $t1, 56
slt $t2, $t1, $t0
# Evaluación de !
lw $t0, ((_func2_(...) > 56))
seq $t2, $t0, $zero
# Evaluación de &&
lw $t0, (_fl1_ || false)
lw $t1, !(((_func2_(...) > 56)))
and $t2, $t0, $t1
# Evaluación de >=
lw $t0, _in1_
lw $t1, ((_fl1_ || false) && !(((_func2_(...) > 56))))
slt $t3, $t0, $t1
xori $t2, $t3, 1
# Asignación a variable _bl1_
sw $t2, _bl1_
# Acceso a variable _bl1_
lw $t2, _bl1_
move $v0, _bl1_
j exit_function
# Inicio de bloque
# Fin de bloque

# Fin del programa - syscall de salida
li $v0, 10
syscall
