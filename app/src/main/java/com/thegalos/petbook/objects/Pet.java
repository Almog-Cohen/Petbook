package com.thegalos.petbook.objects;

public class Pet {
    private String name;
    private String age;
    private String animalType;
    private String gender;
    private Boolean vaccine;
    private Boolean pureBred;
    private String breed;
    private String currentImagePath;
    private String petUID;

    public Pet() {
    }

    public String getName() {
        return name;
    }
    public String getAge() {
        return age;
    }
    public String getAnimalType() {
        return animalType;
    }
    public String getGender() {
        return gender;
    }
    public String getBreed() {
        return breed;
    }
    public Boolean getVaccine() {
        return vaccine;
    }
    public Boolean getPureBred() {
        return pureBred;
    }
    public String getCurrentImagePath() {
        return currentImagePath;
    }
    public String getPetUID() {
        return petUID;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setAge(String age) {
        this.age = age;
    }
    public void setAnimalType(String animalType) {
        this.animalType = animalType;
    }
    public void setGender(String gender) {
        this.gender = gender;
    }
    public void setBreed(String breed) {
        this.breed = breed;
    }
    public void setVaccine(Boolean vaccine) {
        this.vaccine = vaccine;
    }
    public void setPureBred(Boolean pureBred) {
        this.pureBred = pureBred;
    }
    public void setCurrentImagePath(String currentImagePath) {
        this.currentImagePath = currentImagePath;
    }
    public void setPetUID(String petUID) {
        this.petUID = petUID;
    }



}
