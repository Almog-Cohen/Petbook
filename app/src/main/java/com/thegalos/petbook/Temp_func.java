package com.thegalos.petbook;

public class Temp_func {
    //Add Feed
    /* private void addToStorage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (uri != null) {

            final StorageReference fileReference = FirebaseStorage.getInstance().getReference().child(user.getUid()).child("Images").child("galos");

            fileReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Images").setValue(uri);
                            Toast.makeText(getContext(), "image upload", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                            ft.replace(R.id.flNews, new MainFeed()).commit();
;
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "fail to upload image", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                    FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Images").setValue("null");
                    FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                    ft.replace(R.id.flNews, new MainFeed()).commit();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    //TODO Spinning waiting
                    progressBar.setVisibility(View.VISIBLE);

                }
            });
        }
        else{
            Toast.makeText(getContext(), "No photo Selected", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            FirebaseDatabase.getInstance().getReference().child(user.getUid()).child("Images").setValue("empty");
            FragmentTransaction ft = getParentFragmentManager().beginTransaction();
            ft.replace(R.id.flNews, new MainFeed()).commit();
        }
    }
*/

}
