*** Settings ***
Library           RequestsLibrary

*** Test Cases ***
Get Pet by ID
    [Tags]    Tomek
    Create Session    petstore    https://petstore.swagger.io/v2
    ${response}=    Get Request    petstore    /pet/1
    Should Be Equal As Integers    ${response.status_code}    200