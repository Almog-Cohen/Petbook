package com.thegalos.petbook.Objects;

public class Feed {
    private String postOwner;
    private String postText;
    private String selectedPet;
    private String imageURL;

    public Feed() {
    }

    //
    public String getPostOwner() {
        return postOwner;
    }
//
    public String getPostText() {
        return postText;
    }
//
    public String getSelectedPet() {
        return selectedPet;
    }

    public String getImageURL() {
        return imageURL;
    }



    public void setPostOwner(String postOwner) {
        this.postOwner = postOwner;
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




}
