.data
_entero1_: .word 0

.text
.globl main
main:
# Reserva para variable _entero1_
# Cargar literal entero 5
li $t2, 5
# Asignación a variable _entero1_
sw $t2, _entero1_
# Acceso a variable _temp_
lw $t2, _temp_
# Cargar literal entero 0
li $t2, 0
# Asignación a variable _entero1_
sw $t2, _entero1_
# Inicio de bloque
# Fin de bloque
# Comprobando case 0
li $t1, 0
beq $t0, $t1, case_0_0
case_0_0:
null
j null
# Cargar literal entero 1
li $t2, 1
# Asignación a variable _entero1_
sw $t2, _entero1_
# Inicio de bloque
# Fin de bloque
# Comprobando case 1
li $t1, 1
beq $t0, $t1, case_1_1
case_1_1:
null
j null
# Cargar literal entero 2
li $t2, 2
# Asignación a variable _entero1_
sw $t2, _entero1_
# Inicio de bloque
# Fin de bloque
# Comprobando case 2
li $t1, 2
beq $t0, $t1, case_2_2
case_2_2:
null
j null
# Evaluar expresión del switch
lw $t0, _temp_
# Inicio de los case blocks
case 0: null
case 1: null
case 2: null
switch_exit_3:
# Acceso a variable _entero1_
lw $t2, _entero1_
move $v0, _entero1_
j exit_function
# Inicio de bloque
# Fin de bloque

# Fin del programa - syscall de salida
li $v0, 10
syscall
