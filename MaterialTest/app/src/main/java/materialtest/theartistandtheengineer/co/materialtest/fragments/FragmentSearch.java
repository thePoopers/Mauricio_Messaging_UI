package materialtest.theartistandtheengineer.co.materialtest.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

import materialtest.theartistandtheengineer.co.materialtest.R;
import materialtest.theartistandtheengineer.co.materialtest.adapters.AdapterSearch;
import materialtest.theartistandtheengineer.co.materialtest.logging.L;
import materialtest.theartistandtheengineer.co.materialtest.materialtest.MyApplication;
import materialtest.theartistandtheengineer.co.materialtest.network.VolleySingleton;
import materialtest.theartistandtheengineer.co.materialtest.pojo.Book;

/*
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
*/

public class FragmentSearch extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static final String URL_BOOK = "https://www.googleapis.com/books/v1/volumes";
    public static final String URL_BOOK_SEARCH = "q=";
    public static final String URL_BOOK_CONTENTS = "the+kite+runner";
    public static final String URL_BOOK_START_INDEX = "startIndex=";
    public static final String URL_BOOK_MAX_RESULTS = "maxResults=";
    public static final String URL_BOOK_PARAM_API_KEY = "key=";
    public static final String URL_CHAR_QUESTION = "?";
    public static final String URL_CHAR_AMPERSAND = "&";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private VolleySingleton volleySingleton;
    private ImageLoader imageLoader;
    private RequestQueue requestQueue;
    private ArrayList<Book> listBooks = new ArrayList<>();
    private RecyclerView listSearchedBooks;
    private AdapterSearch adapterSearch;

    /*
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSearch.
     */


    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static String getRequestUrl(int startIndex, int maxResults){

        return URL_BOOK
                + URL_CHAR_QUESTION
                + URL_BOOK_SEARCH
                + URL_BOOK_CONTENTS
                + URL_CHAR_AMPERSAND
                + URL_BOOK_START_INDEX
                + startIndex
                + URL_CHAR_AMPERSAND
                +URL_BOOK_MAX_RESULTS
                + maxResults
                + URL_CHAR_AMPERSAND
                + URL_BOOK_PARAM_API_KEY
                + MyApplication.API_KEY_GOOGLE_BOOKS;
    }

    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        volleySingleton = VolleySingleton.getInstance();
        requestQueue = volleySingleton.getRequestQueue();
        sendJsonRequest();
    }

    private void sendJsonRequest() {

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                getRequestUrl(0, 20),
                (String) null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        listBooks = parseJSONResponse(response);
                        adapterSearch.setBookList(listBooks);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

    private ArrayList<Book> parseJSONResponse(JSONObject response){
        ArrayList<Book> listBooks = new ArrayList<>();

        try {
            StringBuilder data = new StringBuilder();
            // If there are results
            if(response.has("items")){
                // store all of the results in an JSON array
                JSONArray arrayBooks = response.getJSONArray("items");
                // loop through each of the results(array)
                for(int i = 0; i < arrayBooks.length(); i++){

                    JSONObject currentBook = arrayBooks.getJSONObject(i);
                    String id = currentBook.getString("id");
                    // make the volumeInfo JSON Object
                    JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");

                    // title
                    String volumeTitle = volumeInfo.getString("title");
                    // author
                    JSONArray volumeAuthor = volumeInfo.getJSONArray("authors");
                    String author = volumeAuthor.getString(0);
                    // isbn's
                    JSONArray volumeIndustryIdentifier = volumeInfo.getJSONArray("industryIdentifiers");
                    JSONObject isbn_type1 = volumeIndustryIdentifier.getJSONObject(0);
                    JSONObject isbn_type2 = volumeIndustryIdentifier.getJSONObject(1);

                    String isbn1 = isbn_type1.getString("identifier");
                    String isbn2 = isbn_type2.getString("identifier");
                    String isbn = null;

                    if(isbn1.length() > 10) {
                        isbn = isbn1;
                    }else{
                        isbn = isbn2;
                    }

                    //urlthumbnail
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    String urlThumbnail = imageLinks.getString("thumbnail");
                    Book book = new Book();
                    book.setTitle(volumeTitle);
                    book.setAuthors(author);
                    book.setISBN_13(isbn);
                    book.seturlThumbnail(urlThumbnail);

                    listBooks.add(book);
                    //date stuff at end of video 37
                    //data.append(id + "\n" + volumeTitle + "\n" + author + "\n" + identifier + "\n");
                }
                //L.T(getActivity(), listBooks.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listBooks;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        listSearchedBooks = (RecyclerView) view.findViewById(R.id.listSearchedBooks);
        listSearchedBooks.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapterSearch = new AdapterSearch(getActivity());
        listSearchedBooks.setAdapter(adapterSearch);
        sendJsonRequest();
        return view;
    }


}