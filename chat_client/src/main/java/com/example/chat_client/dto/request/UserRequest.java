// FILE: 
// Author: vamamurin
/*
tree:
        UserRequest
        ├── [FIELDS - private]
        │   ├── Long id
        │   ├── String userName
        │   └── String userFullName
        │
        ├── [PUBLIC CONSTRUCTOR]
        │   └── UserDTO(User user) → Map dữ liệu từ Entity User sang DTO
        │
        ├── [PUBLIC GETTERS]
        │   ├── getId() 
        │   ├── getUserName() 
        │   └── getUserFullName()
        │
        ├── [PUBLIC SETTERS]
        │   ├── setId(Long id)
        │   ├── setUserName(String userName)
        │   └── setFullName(String userFullName)
        │
        └── END
*/


package com.example.chat_client.dto.request;

public class UserRequest {
    
    private String userName;

    public UserRequest(){}

    public UserRequest(String userName){
        this.userName = userName;
    }

    // Getter / Setter
    public String getUserName(){ return userName; }
    public void setUserName(String userName){ this.userName = userName; }
}
