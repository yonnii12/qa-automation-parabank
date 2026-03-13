@ui @smoke @register
Feature: Registro de usuario en ParaBank

  Scenario: Registro exitoso con datos válidos
    Given que el usuario ingresa a la página de registro
    When diligencia el formulario con datos válidos
    And envía el formulario de registro
    Then el sistema debe crear la cuenta correctamente