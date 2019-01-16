package cam.equipment.life.com.equipmentlifecam.free.main;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.transition.Fade;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.database.AppEquipmentLifeDatabase;
import cam.equipment.life.com.equipmentlifecam.executors.AppExecutors;
import cam.equipment.life.com.equipmentlifecam.free.adapter.EquipmentAdapter;
import cam.equipment.life.com.equipmentlifecam.free.equipment.details.EquipmentDetailsActivity;
import cam.equipment.life.com.equipmentlifecam.free.equipment.registration.EquipmentRegistrationActivity;
import cam.equipment.life.com.equipmentlifecam.free.listeners.OnEquipmentItemSelectedListener;
import cam.equipment.life.com.equipmentlifecam.free.owner.details.ProfileDetailsActivity;
import cam.equipment.life.com.equipmentlifecam.free.session.SessionManager;
import cam.equipment.life.com.equipmentlifecam.listeners.OnPostEquipmentListTaskListener;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.model.Profile;
import cam.equipment.life.com.equipmentlifecam.utils.EquipmentListAsyncTask;
import cam.equipment.life.com.equipmentlifecam.viewmodel.EquipmentListWithQueryViewModel;
import cam.equipment.life.com.equipmentlifecam.viewmodel.EquipmentViewModel;
import cam.equipment.life.com.equipmentlifecam.viewmodel.ProfileDetailViewModel;
import cam.equipment.life.com.equipmentlifecam.viewmodel.factory.ProfileViewModelFactory;

import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.AMOUNT_ITEMS_STORED;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FLAG_ITEMS_STORED;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_FACEBOOK_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.FROM_GOOGLE_SIGNUP;
import static cam.equipment.life.com.equipmentlifecam.free.session.SessionManager.KEY_EMAIL;

public class MainCamActivity extends AppCompatActivity implements OnEquipmentItemSelectedListener,
        NavigationView.OnNavigationItemSelectedListener, OnPostEquipmentListTaskListener {

    // Constant for logging
    private static final String TAG = MainCamActivity.class.getSimpleName();

    // Member variables for the adapter and RecyclerView
    @BindView(R.id.rv_equipment_list) RecyclerView mRecyclerViewEquipmentList;

    @BindView(R.id.swipe_refresh_layout_main) SwipeRefreshLayout swipeRefreshLayoutMain;

    // Equipment Adapter to show the list items
    private EquipmentAdapter mEquipmentAdapter;

    @BindView(R.id.fab_add_new_equipment) FloatingActionButton fabButtonAddNewEquipment;

    @BindView(R.id.toolbar_equipment_life_main_screen) Toolbar toolbarEquipmentLife;
    @BindView(R.id.drawer_layout_equipment_life) DrawerLayout drawerEquipmentLife;
    @BindView(R.id.nav_view_equipment_life) NavigationView navigationEquipmentLifeView;

    @BindView(R.id.layout_equipment_empty_list_text) LinearLayout layoutEquipmentEmptyListText;

    private ActionBarDrawerToggle toggleEquipmentLife;

    // Member variable for the database
    private AppEquipmentLifeDatabase mDb;

    private Profile profileOwner;

    private int amountItemStored;

    private boolean isAmountLimit;

    // Session Manager Class
    private SessionManager session;

    private SearchView searchView;

    List<Equipment> equipmentList;

    // Flag to check if login was from Google
    private boolean isLoginFromGoogle;

    // Flag to check if login was from facebook
    private boolean isLoginFromFacebook;

    private EquipmentListAsyncTask equipmentListAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_cam);

        ButterKnife.bind(this);

        setupTransition();

        setSupportActionBar(toolbarEquipmentLife);

        mDb = AppEquipmentLifeDatabase.getsInstance(getApplicationContext());

        // Session Manager
        session = new SessionManager(getApplicationContext());

        // Check if the login was from Google
        isLoginFromGoogle = session.pref.getBoolean(FROM_GOOGLE_SIGNUP, false);

        // Check if the login was from Facebook
        isLoginFromFacebook = session.pref.getBoolean(FROM_FACEBOOK_SIGNUP, false);

        navigationEquipmentLifeView.setNavigationItemSelectedListener(this);

        toggleEquipmentLife = new ActionBarDrawerToggle(this, drawerEquipmentLife, toolbarEquipmentLife,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerEquipmentLife.addDrawerListener(toggleEquipmentLife);

        toggleEquipmentLife.syncState();

        int amountLimit = session.pref.getInt(AMOUNT_ITEMS_STORED, 0);
        boolean isLimit = session.pref.getBoolean(FLAG_ITEMS_STORED, false);

        String email = session.pref.getString(KEY_EMAIL, "");

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {

            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {

                profileOwner = (Profile) intentThatStartedThisActivity.getSerializableExtra(Intent.EXTRA_TEXT);

            } else {

                ProfileViewModelFactory factory = new ProfileViewModelFactory(mDb, email);

                final ProfileDetailViewModel viewModel =
                        ViewModelProviders.of(this, factory).get(ProfileDetailViewModel.class);

                viewModel.getProfile().observe(this, new Observer<Profile>() {

                    @Override
                    public void onChanged(@Nullable Profile profile) {

                        viewModel.getProfile().removeObserver(this);

                        addingRetrievedProfileInVariable(profile);

                    }
                });

            }

        }

        mEquipmentAdapter = new EquipmentAdapter(this, this);

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerViewEquipmentList.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerViewEquipmentList.setHasFixedSize(true);
        mRecyclerViewEquipmentList.setItemAnimator(new DefaultItemAnimator());
        mRecyclerViewEquipmentList.setAdapter(mEquipmentAdapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView,
                                  @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int swipeDir) {

                AppExecutors.getsInstance().getDiskIO().execute(() -> {

                    int position = viewHolder.getAdapterPosition();

                    List<Equipment> equipments = mEquipmentAdapter.getEquipmentList();

                    mDb.equipmentDao().deleteEquipment(equipments.get(position));

                });

            }
        }).attachToRecyclerView(mRecyclerViewEquipmentList);

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
         fabButtonAddNewEquipment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (amountLimit == 10 && isLimit) {

                    Toast.makeText(MainCamActivity.this, "", Toast.LENGTH_LONG).show();

                } else {

                    // Create a new intent to start an EquipmentRegistrationActivity
                    Intent addEquipmentIntent = new Intent(MainCamActivity.this, EquipmentRegistrationActivity.class);
                    startActivity(addEquipmentIntent);

                }

            }
        });

        swipeRefreshLayoutMain.setColorSchemeResources(R.color.theme_accent, R.color.light_blue, R.color.blue);
        swipeRefreshLayoutMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //handling swipe refresh
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayoutMain.setRefreshing(false);
                        setupViewModel();
                    }
                }, 2000);
            }
        });

        AdView mAdView = findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        asyncTaskGetAllEquipments();

    }

    @Override
    public void onBackPressed() {
        // disable going back to the Login Activity
        moveTaskToBack(true);
    }

    // To update list of equipments with LiveData in viewModel
    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart() inside method");
        setupViewModel();
    }

    private void setupViewModel() {

        EquipmentViewModel viewModel = ViewModelProviders.of(this).get(EquipmentViewModel.class);

        // This observable is executed out of the main UI thread by default
        viewModel.getAllEquipments().observe(this, equipments -> {

            if (!equipments.isEmpty())
                layoutEquipmentEmptyListText.setVisibility(View.INVISIBLE);

            mEquipmentAdapter.setEquipments(equipments);
        });
    }

    /**
     * This method fetches all equipments and put them in a list to be shown on recyclerview
     */
    private void asyncTaskGetAllEquipments() {

        equipmentListAsyncTask = new EquipmentListAsyncTask(this, mDb);
        equipmentListAsyncTask.execute();

    }

    /**
     * This method is used to select an item from the recycler view and pass it to the details activity
     *
     * @param equipment the equipment object got from adapter
     * @param position the position clicked by the user on screen
     */
    @Override
    public void onEquipmentItemSelected(Equipment equipment, int position) {
        Log.i(TAG, "onEquipmentItemSelected() inside method");

        Context context = this;
        Class destinationClass = EquipmentDetailsActivity.class;

        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, equipment);

        View view = findViewById(R.id.layout_equipment_list_item);
        ActivityOptionsCompat optionsCompat =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this, view, getString(R.string.transition_image));
        startActivity(intentToStartDetailActivity);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();

        if (isLoginFromGoogle || isLoginFromFacebook) {

            inflater.inflate(R.menu.profile_menu_free, menu);

        } else {

            inflater.inflate(R.menu.profile_menu, menu);

        }


        searchView = (SearchView) menu.findItem(R.id.action_equipment_search).getActionView();

        searchView.setSubmitButtonEnabled(true);

        searchView.setOnQueryTextListener(onQueryTextListener);

        return super.onCreateOptionsMenu(menu);

    }

    private SearchView.OnQueryTextListener onQueryTextListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {

            getEquipmentsFromDb(query);

            return true;

        }

        @Override
        public boolean onQueryTextChange(String newText) {

            getEquipmentsFromDb(newText);

            return true;

        }

        private void getEquipmentsFromDb(String searchText) {

            searchText = "%"+searchText+"%";

            setupEquipmentViewModel(searchText);
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_profile) {

            Context context = this;
            Class destinationClass = ProfileDetailsActivity.class;

            Intent intentToStartDetailActivity = new Intent(context, destinationClass);
            intentToStartDetailActivity.putExtra(Intent.EXTRA_TEXT, profileOwner);

            startActivity(intentToStartDetailActivity);

            return true;

        }

        if (item.getItemId() == R.id.action_profile_logout) {

            if (isLoginFromFacebook || isLoginFromGoogle) {

                session.logoutUserFromSocial();

            } else {

                session.logoutUser();
            }

            finish();

        }

        return super.onOptionsItemSelected(item);
        
    }

    private void addingRetrievedProfileInVariable(Profile profile) {

        profileOwner = profile;

    }

    private void setupEquipmentViewModel(String query) {
        EquipmentListWithQueryViewModel viewModel = ViewModelProviders.of(this).get(EquipmentListWithQueryViewModel.class);

        // This observable is executed out of the main UI thread by default
        viewModel.getEquipmentsListWithQuery(query).observe(this, equipments -> {
            Log.d(TAG, "Updating list of equipments from LiveData in ViewModel");
            mEquipmentAdapter.setEquipments(equipments);
        });
    }

    private void getEquipmentByStatusViewModel(String status) {

        EquipmentListWithQueryViewModel viewModel = ViewModelProviders.of(this).get(EquipmentListWithQueryViewModel.class);

        // This observable is executed out of the main UI thread by default
        viewModel.getEquipmentsListByStatus(status).observe(this, equipments -> {
            Log.d(TAG, "Updating list of equipments by status from LiveData in ViewModel");

            mEquipmentAdapter.setEquipments(equipments);

        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_equipment_owned) {
            getEquipmentOwned(getResources().getString(R.string.option_equipment_status_owned));

        } else if (id == R.id.nav_equipment_sold) {
            getEquipmentSold(getResources().getString(R.string.option_equipment_status_sold));

        } else if (id == R.id.nav_equipment_stolen) {
            getEquipmentStolen(getResources().getString(R.string.option_equipment_status_stolen));

        }

        drawerEquipmentLife.closeDrawer(GravityCompat.START);

        return true;

    }

    private void getEquipmentStolen(String string) {

        getEquipmentByStatusViewModel(string);

    }

    private void getEquipmentSold(String string) {

        getEquipmentByStatusViewModel(string);

    }

    private void getEquipmentOwned(String string) {

        getEquipmentByStatusViewModel(string);

    }

    private void setupTransition(){
        Fade fade = (Fade) TransitionInflater.from(this).inflateTransition(R.transition.grid_exit);
        getWindow().setExitTransition(fade);
    }

    @Override
    public void onTaskCompleted(List<Equipment> equipments) {

        if (equipments.size() == 10) {

            amountItemStored = equipments.size();
            isAmountLimit = true;

            session.editor.putInt(AMOUNT_ITEMS_STORED, amountItemStored);
            session.editor.putBoolean(FLAG_ITEMS_STORED, isAmountLimit);

        } else if ((equipments.isEmpty())) {

            layoutEquipmentEmptyListText.setVisibility(View.VISIBLE);

        }

        equipmentList = equipments;

        mEquipmentAdapter.setEquipments(equipments);

    }
}
