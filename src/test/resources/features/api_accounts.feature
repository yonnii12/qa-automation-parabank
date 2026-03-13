@api @smoke
Feature: Consulta de cuentas por API

  Scenario: Obtener cuentas de un cliente autenticado
    Given que existe un usuario válido para consumir la API
    When consulto el servicio de login
    And consulto el servicio getAccounts con el customerId obtenido
    Then la respuesta debe ser exitosa
    And la lista de cuentas no debe estar vacía