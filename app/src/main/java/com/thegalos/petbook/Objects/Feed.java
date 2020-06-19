package com.thegalos.petbook.Objects;

public class Feed {
    private String postOwner;
    private String OwnerUID;
    private String postText;
    private String selectedPet;
    private String imageURL;




    private Pet pet;

    public Feed() {
    }

    public String getPostOwner() {
        return postOwner;
    }
    public String getOwnerUID() {
        return OwnerUID;
    }
    public String getPostText() {
        return postText;
    }
    public String getSelectedPet() {
        return selectedPet;
    }
    public String getImageURL() {
        return imageURL;
    }
    public Pet getPet() {
        return pet;
    }

    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
    }
    public void setOwnerUID(String ownerUID) {
        OwnerUID = ownerUID;
    }
    public void setPostText(String postText) {
        this.postText = postText;
    }
    public void setSelectedPet(String selectedPet) {
        this.selectedPet = selectedPet;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
    public void setPet(Pet pet) {
        this.pet = pet;
    }

}
