*** Settings ***
Library           RequestsLibrary

*** Variables ***
&{HEADERS}        Content-Type=application/json

*** Test Cases ***
Get Pet by ID 2
    [Tags]    Tomek    smoke    all
    Create Session    petstore    https://petstore.swagger.io/v2    verify=${False}
    ${body}=    Create Dictionary    id=${2}    name=Doggie    photoUrls=@{EMPTY}    status=available
    POST On Session    petstore    /pet    json=${body}    headers=${HEADERS}
    ${response}=    GET On Session    petstore    /pet/2
    Should Be Equal As Integers    ${response.status_code}    200
    
Get Pet by ID 3
    [Tags]    Tomek    regression    all
    Create Session    petstore    https://petstore.swagger.io/v2    verify=${False}
    ${body}=    Create Dictionary    id=${2}    name=Doggie    photoUrls=@{EMPTY}    status=available
    POST On Session    petstore    /pet    json=${body}    headers=${HEADERS}
    ${response}=    GET On Session    petstore    /pet/2
    Should Be Equal As Integers    ${response.status_code}    200