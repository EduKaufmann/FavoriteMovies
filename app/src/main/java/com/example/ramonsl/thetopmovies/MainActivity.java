package com.example.ramonsl.thetopmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ramonsl.thetopmovies.Adapter.MoviesAdapter;
import com.example.ramonsl.thetopmovies.Data.Movie;
import com.example.ramonsl.thetopmovies.Data.MoviesDTO;
import com.example.ramonsl.thetopmovies.Transport.RetrofitConfig;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {


    private static final String API_KEY = "d15439ba9445688264047fbb91fce4c4";
    private MoviesDTO mList;
    private CoordinatorLayout coordinatorLayout;


    private RecyclerView mRecyclerView;
    private MoviesAdapter mMovieAdapter;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private ArrayList<Movie> dataMovies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recyclerview_movies);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);


        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns(), GridLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MoviesAdapter();
        mRecyclerView.setAdapter(mMovieAdapter);


        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (VerificaConexao(this)) {
            getPopularMovies();


        } else {
            showErrorMessage("Ative sua conexão de dados ou WIFI");
        }


    }

    private void showErrorMessage(String msg) {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        Snackbar snackbarError = Snackbar.make(coordinatorLayout, msg, Snackbar.LENGTH_LONG);
        snackbarError.show();
    }

    private void getPopularMovies() {
        showProgressBar();
        Call<MoviesDTO> call = new RetrofitConfig().get().retrievePopularMovies(API_KEY);
        call.enqueue(new Callback<MoviesDTO>() {
            @Override
            public void onResponse(Call<MoviesDTO> call, Response<MoviesDTO> response) {
                Log.i("CALLBACK", response.message());

                if (response.isSuccessful() == true) {
                    if (response.body() != null) {
                        mList = response.body();

                    }

                    mMovieAdapter.setMovieData((ArrayList<Movie>) mList.getMovies());
                    hideProgressBar();

                } else {
                    showErrorMessage("Indiponibilidade de buscar os filmes");
                }


            }

            @Override
            public void onFailure(Call<MoviesDTO> call, Throwable t) {
                Log.e("CALLBACK", t.getMessage());
                showErrorMessage("Erro na requisição");
            }
        });
    }

    private void getTopRatedMovies() {

        showProgressBar();
        Call<MoviesDTO> call = new RetrofitConfig().get().retriveTopRatedMovies(API_KEY);


        call.enqueue(new Callback<MoviesDTO>() {
            @Override
            public void onResponse(Call<MoviesDTO> call, Response<MoviesDTO> response) {
                if (response.isSuccessful() == true) {
                    if (response.body() != null) {
                        mList = response.body();

                    }
                    mMovieAdapter.setMovieData((ArrayList<Movie>) mList.getMovies());
                    hideProgressBar();
                } else {
                    showErrorMessage("Indiponibilidade de buscar os filmes");
                }
            }

            @Override
            public void onFailure(Call<MoviesDTO> call, Throwable t) {
                Log.e("CALLBACK", t.getMessage());
                showErrorMessage("Erro na requisição");
            }


        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_pop) {

            getPopularMovies();
            hideProgressBar();

            return true;
        }
        if (id == R.id.action_top) {



            getTopRatedMovies();
            hideProgressBar();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2;
        return nColumns;
    }


    public boolean VerificaConexao(Context contexto) {
        boolean conectado = false;
        ConnectivityManager conmag;
        conmag = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        conmag.getActiveNetworkInfo();
        //Verifica o WIFI
        if (conmag.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()) {
            conectado = true;
        }
        //Verifica o 3G
        else if (conmag.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()) {
            conectado = true;
        } else {
            conectado = false;
        }
        return conectado;
    }

    private void showProgressBar() {
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        mLoadingIndicator.setVisibility(View.GONE);
    }
}
