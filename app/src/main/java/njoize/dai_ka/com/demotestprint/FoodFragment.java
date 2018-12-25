package njoize.dai_ka.com.demotestprint;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FoodFragment extends Fragment {

    //    Explicit
    private String amountCustomerString;
    private boolean totalBillABoolean;
    private String idCategoryClick;

    public FoodFragment() {
        // Required empty public constructor
    }

    public static FoodFragment foodInstante(String amountCustomer, boolean totalBill) {

        FoodFragment foodFragment = new FoodFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Amount", amountCustomer);
        bundle.putBoolean("Bill", totalBill);
        foodFragment.setArguments(bundle);
        return foodFragment;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        Receive Value
        receiveValue();

//        Category RecyclerView
        categoryRecyclerView();


    }   // Main Method

    private void foodRecyclerView(String idCategoryString) {
        RecyclerView recyclerView = getView().findViewById(R.id.recyclerFood);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        MyConstant myConstant = new MyConstant();
        SharedPreferences sharedPreferences = getActivity()
                .getSharedPreferences(myConstant.getSharePreferFile(), Context.MODE_PRIVATE);
        String userLogin = sharedPreferences.getString("User", "");

        Log.d("25decV1", "idCat ==> " + idCategoryString);
        Log.d("25decV1", "userLogin ==> " + userLogin);

        final ArrayList<String> foodStringArrayList = new ArrayList<>();
        ArrayList<String> priceStringArrayList = new ArrayList<>();

        try {

            GetFoodWhereIdAndUser getFoodWhereIdAndUser = new GetFoodWhereIdAndUser(getActivity());
            getFoodWhereIdAndUser.execute(idCategoryString, userLogin, myConstant.getUrlGetFoodWhereIdAndUser());
            String jsonString = getFoodWhereIdAndUser.get();
            Log.d("25decV1", "jsonString123 ==> " + jsonString);

            if (jsonString.equals("null")) {
                recyclerView.setAdapter(null);
            }

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i += 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                foodStringArrayList.add(jsonObject.getString("pname"));
                priceStringArrayList.add(jsonObject.getString("price"));
            }

            Log.d("25decV1", "food ==> " + foodStringArrayList.toString());
            FoodAdapter foodAdapter = new FoodAdapter(getActivity(), foodStringArrayList, priceStringArrayList, new OnClickItem() {
                @Override
                public void onClickItem(View view, int positions) {
                    Log.d("25DecV2", "Food Choose ==> " + foodStringArrayList.get(positions));
                }
            });

            recyclerView.setAdapter(foodAdapter);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("25decV2", "e ==> " + e.toString());
        }




    }

    private void categoryRecyclerView() {

        RecyclerView recyclerView = getView().findViewById(R.id.recyclerCategory);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        try {

            MyConstant myConstant = new MyConstant();
            GetAllData getAllData = new GetAllData(getActivity());
            getAllData.execute(myConstant.getUrlGetCategoryString());

            String jsonString = getAllData.get();
            Log.d("24decV3", "jsonString ==> " + jsonString);

            ArrayList<String> categoryStringArrayList = new ArrayList<>();
            final ArrayList<String> idCategoryStringArrayList = new ArrayList<>();

            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i += 1) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                categoryStringArrayList.add(jsonObject.getString("prcname"));
                idCategoryStringArrayList.add(jsonObject.getString("id"));
            }

            CategoryAdapter categoryAdapter = new CategoryAdapter(getActivity(), categoryStringArrayList, new OnClickItem() {
                @Override
                public void onClickItem(View view, int positions) {
                    Log.d("24decV3", "Position ==> " + positions);
                    foodRecyclerView(idCategoryStringArrayList.get(positions));
                }
            });

            recyclerView.setAdapter(categoryAdapter);

            foodRecyclerView(idCategoryStringArrayList.get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void receiveValue() {
        amountCustomerString = getArguments().getString("Amount");
        totalBillABoolean = getArguments().getBoolean("Bill");
        Log.d("24decV3", "amount ==> " + amountCustomerString);
        Log.d("24decV3", "Bill ==> " + totalBillABoolean);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_food, container, false);
    }

}
