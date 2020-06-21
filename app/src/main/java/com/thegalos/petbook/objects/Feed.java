package com.thegalos.petbook.objects;

public class Feed {
    private String postOwner;
    private String OwnerUID;
    private String postText;
    private String selectedPet;
    private String imageURL;
    private String free;
    private String whoPays;
    private String time;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String amount;



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
    public String getFree() {
        return free;
    }
    public String getAmount() {
        return amount;
    }
    public String getWhoPays() {
        return whoPays;
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
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public void setFree(String free) {
        this.free = free;
    }
    public void setWhoPays(String whoPays) {
        this.whoPays = whoPays;
    }


}
