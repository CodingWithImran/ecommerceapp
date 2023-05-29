package com.codingwithimran.fycommerce.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.codingwithimran.fycommerce.Modals.PopularProductModal;
import com.codingwithimran.fycommerce.Modals.ProductModal;
import com.codingwithimran.fycommerce.Modals.ShowAllProductModal;
import com.codingwithimran.fycommerce.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class DetailActivity extends AppCompatActivity {
    ProductModal productModal = null;
    PopularProductModal popularProductModal = null;
    ShowAllProductModal showAllProductModal = null;
    ImageView productimg, addquantity, removequantity;
    TextView description, name, price, quantity;
    Button addtocart, buyNowbtn;
    FirebaseFirestore database;
    FirebaseAuth auth;
    VideoView productVideo;
    VideoView videoView;
    ProgressDialog progressDialog;
    int totalQuantity = 0;
    int totalPrice = 0;

    TextView mystockProduct;
    // For ExoPlayer Video
    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;
    int stock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        // Code For ToolBar
        Toolbar toolbar = findViewById(R.id.detail_activity_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get data from Layout
        productimg = findViewById(R.id.productImage);
        description = findViewById(R.id.productDescription);
        name = findViewById(R.id.name_product);
        price = findViewById(R.id.price_product);
        buyNowbtn = findViewById(R.id.btn_buyNow);
        addtocart = findViewById(R.id.addToCartBtn);
        addquantity = findViewById(R.id.plus);
        removequantity = findViewById(R.id.minus);
        quantity = findViewById(R.id.Quantity);
        productVideo = findViewById(R.id.productVideo);
        videoView = findViewById(R.id.productVideo);
        mystockProduct = findViewById(R.id.StockProduct);

        // For Video FindViewById
        playerView = findViewById(R.id.playerView);

        // Instance for Firebase
        database = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Waite...");
        // Create a new instance of the ExoPlayer
        exoPlayer = new SimpleExoPlayer.Builder(this).build();

        // Attach the ExoPlayer to the PlayerView
        playerView.setPlayer(exoPlayer);

       // Get Data From New, Popular and all product by put Extra
        final Object obj = getIntent().getSerializableExtra("newProductDetails");
        final Object showobj = getIntent().getSerializableExtra("showAllDetail");
        final Object allproduct_obj = getIntent().getSerializableExtra("popularProductDetail");

       // Check Instance for Product Modal
        if(obj instanceof ProductModal){
            productModal = (ProductModal) obj;
        } else if (allproduct_obj instanceof PopularProductModal) {
            popularProductModal = (PopularProductModal) allproduct_obj;
        }else if(showobj instanceof ShowAllProductModal){
            showAllProductModal = (ShowAllProductModal) showobj;
        }

        // Get Data from Modal if not null
        if(productModal != null){
            Glide.with(this).load(productModal.getProduct_img()).into(productimg);
            description.setText(productModal.getDescription());
            name.setText(productModal.getName());
            price.setText(String.valueOf(productModal.getPrice()));
            mystockProduct.setText(String.valueOf(productModal.getStockProduct()));
            totalPrice = totalQuantity * productModal.getPrice();
            stock = productModal.getStockProduct();

            // set video or image in product Modal

            String imagePath = productModal.getProduct_img();
            String videoPath = productModal.getProduct_video();

            if(imagePath != null){
                Glide.with(this).load(imagePath).into(productimg);
                productimg.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
                Toast.makeText(this, "image path is not null", Toast.LENGTH_SHORT).show();
            }
            else
                if(videoPath != null){
                try {
                    prepareVideo(videoPath);
                    playerView.setVisibility(View.VISIBLE);
                    productimg.setVisibility(View.GONE);
//                    videoView.start();

                }catch (Exception e){
                    Toast.makeText(this, "Video not displayed due to some problems", Toast.LENGTH_SHORT).show();
                }
            }
        }
        if (popularProductModal !=null) {
            description.setText(popularProductModal.getDescription());
            name.setText(popularProductModal.getName());
            price.setText(String.valueOf(popularProductModal.getPrice()));
            mystockProduct.setText(String.valueOf(popularProductModal.getStockProduct()));
            totalPrice = totalQuantity * popularProductModal.getPrice();
            stock = popularProductModal.getStockProduct();
            //  set video and image in popularProduct Modal

            String imagePath = popularProductModal.getProduct_img();
            String videoPath = popularProductModal.getProduct_video();

            if(imagePath != null){
                Glide.with(this).load(imagePath).into(productimg);
                productimg.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
//                Toast.makeText(this, "image path is not null", Toast.LENGTH_SHORT).show();
            }
            else if(videoPath != null){
                try {
                    prepareVideo(videoPath);
//                    videoView.setVideoURI(Uri.parse(videoPath));
                    playerView.setVisibility(View.VISIBLE);
                    productimg.setVisibility(View.GONE);
//                    videoView.start();
                }catch (Exception e){
                    Toast.makeText(this, "Video not displayed due to some problems", Toast.LENGTH_SHORT).show();
                }
            }


        }
        if (showAllProductModal !=null) {
            description.setText(showAllProductModal.getDescription());
            name.setText(showAllProductModal.getName());
            price.setText(String.valueOf(showAllProductModal.getPrice()));
            totalPrice = totalQuantity * showAllProductModal.getPrice();
            stock = showAllProductModal.getStockProduct();
            mystockProduct.setText(String.valueOf(showAllProductModal.getStockProduct()));
            String imagePath = showAllProductModal.getProduct_img();
            String videoPath = showAllProductModal.getProduct_video();

            if(imagePath != null){
                Glide.with(this).load(imagePath).into(productimg);
                productimg.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.GONE);
//                Toast.makeText(this, "image path is not null", Toast.LENGTH_SHORT).show();
            }
            else if(videoPath != null){
                try {
                    prepareVideo(videoPath);
//                    videoView.setVideoURI(Uri.parse(videoPath));
                    playerView.setVisibility(View.VISIBLE);
                    productimg.setVisibility(View.GONE);
//                    videoView.start();

                }catch (Exception e){
                    Toast.makeText(this, "Video not displayed due to some problems", Toast.LENGTH_SHORT).show();
                }
                Toast.makeText(this, "video path is not null", Toast.LENGTH_SHORT).show();
            }



        }
        // End For Data from Modal if not null


        addquantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int stockValue = Integer.parseInt(mystockProduct.getText().toString());
                if(totalQuantity < stockValue){
                    totalQuantity = totalQuantity + 1;
                    quantity.setText(String.valueOf(totalQuantity));
                    if(productModal != null){
                        totalPrice = totalQuantity * productModal.getPrice();
                    }
                    if(popularProductModal != null){
                        totalPrice = totalQuantity * popularProductModal.getPrice();
                    }
                    if(showAllProductModal != null){
                        totalPrice = totalQuantity * showAllProductModal.getPrice();
                    }
                }
            }
        });

        removequantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(totalQuantity > 1){
                    totalQuantity = totalQuantity - 1;
                    quantity.setText(String.valueOf(totalQuantity));
                }
            }
        });

        addtocart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth.getCurrentUser() != null){
                    progressDialog.show();
                    addToCartfunct();
                }else{
                    Toast.makeText(DetailActivity.this, "First please creat a account", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailActivity.this, RegistrationActivity.class));
                }
            }
        });

        // Code for Buy Now / Checkout
        buyNowbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth.getCurrentUser() != null){
                    buyNowFunction();
                }else{
                    Toast.makeText(DetailActivity.this, "First please creat a account", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailActivity.this, RegistrationActivity.class));
                }
            }
        });



    }
    private void buyNowFunction() {
       // Declared Hashmap for the store of data

        String saveCurrentTime, saveCurrentDate;

        Calendar calfordate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM, dd, yyyy");
        saveCurrentDate = currentDate.format(calfordate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calfordate.getTime());

        final HashMap<String, Object> buyMap = new HashMap<>();
        if(productModal != null){
            buyMap.put("productNumber", productModal.getProductNumber());
            buyMap.put("productId", productModal.getProductId());
        }
        if(showAllProductModal != null){
            buyMap.put("productNumber", showAllProductModal.getProductNumber());
            buyMap.put("productId", showAllProductModal.getProductId());

        }
        if(popularProductModal != null){
            buyMap.put("productNumber", popularProductModal.getProductNumber());
            buyMap.put("productId", popularProductModal.getProductId());
        }
        buyMap.put("ProductName", name.getText().toString());
        buyMap.put("ProductPrice", price.getText().toString());
        buyMap.put("StockProduct", mystockProduct.getText().toString());
        buyMap.put("currentTime", saveCurrentTime);
        buyMap.put("currentDate", saveCurrentDate);

        buyMap.put("Quantity", totalQuantity);
        buyMap.put("totalPrice", totalPrice);
       // End of Hashmap functions

        Intent intent = new Intent(DetailActivity.this, AddressActivity.class);
//        if(productModal != null){
////            intent.putExtra("items", productModal);
////            intent.putExtra("totalamount", totalPrice);
//        }
//        if(popularProductModal != null){
////            intent.putExtra("items", popularProductModal);
////            intent.putExtra("totalamount", totalPrice);
//        }
//        if(showAllProductModal != null){
////            intent.putExtra("items", showAllProductModal);
////            intent.putExtra("totalamount", totalPrice);
//        }
        intent.putExtra("buyMapItems", buyMap);
        startActivity(intent);
    }
    private void addToCartfunct() {
        String saveCurrentTime, saveCurrentDate;

        Calendar calfordate = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MM, dd, yyyy");
        saveCurrentDate = currentDate.format(calfordate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a");
        saveCurrentTime = currentTime.format(calfordate.getTime());

        final HashMap<String, Object> cartMap = new HashMap<>();
        if(productModal != null){
            cartMap.put("productNumber", productModal.getProductNumber());
            cartMap.put("productId", productModal.getProductId());
        }
        if(showAllProductModal != null){
            cartMap.put("productNumber", showAllProductModal.getProductNumber());
            cartMap.put("productId", showAllProductModal.getProductId());
        }
        if(popularProductModal != null){
            cartMap.put("productNumber", popularProductModal.getProductNumber());
            cartMap.put("productId", popularProductModal.getProductId());
        }
        cartMap.put("ProductName", name.getText().toString());
        cartMap.put("ProductPrice", price.getText().toString());
        cartMap.put("currentTime", saveCurrentTime);
        cartMap.put("currentDate", saveCurrentDate);
        cartMap.put("StockProduct", mystockProduct.getText().toString());
        cartMap.put("Quantity", totalQuantity);
        cartMap.put("totalPrice", totalPrice);
        database.collection("AddToCart").document(auth.getCurrentUser().getUid()).collection("Users")
                .add(cartMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        progressDialog.dismiss();
                        String quantityKey = "Quantity";
                        String stockKey = "StockProduct";
                        Object quantityValue = cartMap.get(quantityKey);
                        Object stockValue = cartMap.get(stockKey);
                        Integer quantity;
                        Integer stock;
                        if(quantityValue instanceof String){
                            String qvalue = (String) quantityValue;
                            quantity = Integer.parseInt(qvalue);
                        }else{
                            Integer qvalue = (Integer) quantityValue;
                            quantity = qvalue;
                        }
                        if(stockValue instanceof String){
                            String svalue = (String) stockValue;
                            stock = Integer.parseInt(svalue);
                        }else{
                            Integer qvalue = (Integer) stockValue;
                            stock = qvalue;
                        }

                        int remainingStock = stock - quantity;
                        updateStockProduct(cartMap.get("productId").toString(), remainingStock);
                        startActivity(new Intent(DetailActivity.this, MainActivity.class));
                        Toast.makeText(DetailActivity.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void prepareVideo(String videoUrl) {
        // Create a MediaSource object using the video's URL
        Uri videoUri = Uri.parse(videoUrl);

        MediaSource mediaSource = new ProgressiveMediaSource.Factory(
                new DefaultDataSourceFactory(DetailActivity.this, "user-agent"))
                .createMediaSource(MediaItem.fromUri(videoUri));
        // Prepare the ExoPlayer to play the video
        exoPlayer.prepare(mediaSource);
    }

    public void updateStockProduct(String productId, int stockValue) {
        // Access the Firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Access the AllProducts collection
        CollectionReference productsCollection = db.collection("AllProducts");
        CollectionReference newproductsCollection = db.collection("NewProducts");

        // Query to find the document with the specified product ID
        productsCollection.whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Iterate over the result set (should be only one document)
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Update the stockProduct field with the new value
                                productsCollection.document(document.getId())
                                        .update("stockProduct", stockValue)                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("FirestoreUpdate", "StockProduct field updated successfully");
                                                } else {
                                                    Log.d("FirestoreUpdate", "Failed to update StockProduct field");
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("FirestoreQuery", "Error getting documents: ", task.getException());
                        }
                    }
                });

        // Query to find the document with the specified product ID
        newproductsCollection.whereEqualTo("productId", productId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Iterate over the result set (should be only one document)
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Update the stockProduct field with the new value
                                productsCollection.document(document.getId())
                                        .update("stockProduct", stockValue)                                      .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("FirestoreUpdate", "StockProduct field updated successfully");
                                                } else {
                                                    Log.d("FirestoreUpdate", "Failed to update StockProduct field");
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("FirestoreQuery", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}

