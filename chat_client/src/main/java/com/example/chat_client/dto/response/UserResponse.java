// FILE: 
// Author: vamamurin
/*
tree:
        UserDTO
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


package com.example.chat_client.dto.response;

public class UserResponse {
    private Long id;
    private String userName;
    private String userFullName;

    public UserResponse(){}

    public UserResponse(Long id, String userName, String userFullName){
        this.id = id;
        this.userName = userName;
        this.userFullName = userFullName;
    }

    // Getter / Setter
    public Long getId(){ return id; }
    public String getUserName(){ return userName; }
    public String getUserFullName(){ return userFullName; }
    public void setId(Long id ){ this.id = id; }
    public void setUserName(String userName){ this.userName = userName; }
    public void setFullName(String userFullName){ this.userFullName = userFullName; }
}
