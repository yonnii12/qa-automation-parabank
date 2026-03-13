@ui @smoke @transfer
Feature: Transferencia entre cuentas

  Scenario: Transferencia exitosa con saldo suficiente
    Given que el usuario inicia sesión correctamente
    And tiene una cuenta origen con saldo suficiente
    And tiene una cuenta destino activa
    When realiza una transferencia con un monto válido
    Then el sistema debe confirmar la transferencia exitosamente