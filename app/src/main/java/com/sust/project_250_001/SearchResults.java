package com.sust.project_250_001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SearchResults extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookAdapter adapter;
    private ArrayList<Book> bookArrayList;
    private String searchText;

    public static final String EXTRA_SEARCHID = "searchText";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_results);


        //Trending books
        recyclerView = findViewById(R.id.search_recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false));
        bookArrayList = new ArrayList<>();
        adapter = new BookAdapter(this, bookArrayList,listener);
        recyclerView.setAdapter(adapter);

        searchText = (String) getIntent().getExtras().get(EXTRA_SEARCHID);

        DatabaseReference mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        Query query = mFirebaseDatabaseReference.child("Books").orderByChild("title").startAt(searchText.toUpperCase()).endAt(searchText.toLowerCase()+"\uf8ff");
        query.addValueEventListener(valueEventListener);
    }


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                bookArrayList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Book book = snapshot.getValue(Book.class);
                        bookArrayList.add(book);
                    }
                    BookAdapter adapter = new BookAdapter(SearchResults.this, bookArrayList,listener);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };


    BookAdapter.OnItemClickListener listener = new BookAdapter.OnItemClickListener() {
        @Override
        public void onItemClick(Book book) {
            Intent i = new Intent(SearchResults.this,BookProfile.class);
            i.putExtra("bookObject",book);
            startActivity(i);
        }
    };
}
