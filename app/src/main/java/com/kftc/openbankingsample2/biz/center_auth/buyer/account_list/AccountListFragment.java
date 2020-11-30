package com.kftc.openbankingsample2.biz.center_auth.buyer.account_list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.data.ApiCallUserMeResponse;
import com.kftc.openbankingsample2.common.util.Utils;
import com.kftc.openbankingsample2.common.util.view.recyclerview.KmRecyclerViewDividerHeight;

import java.util.HashMap;

public class AccountListFragment extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;
    private RecyclerView recyclerView;
    private AccountListAdapter adapter;

    // data
    private Bundle args;
    ApiCallUserMeResponse result;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_account_list, container, false);
        initView();
        return view;
    }

    void initView() {
        String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
        String userSeqNo = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USER_SEQ_NUM);

        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("user_seq_no", userSeqNo);

        showProgress();
        CenterAuthApiRetrofitAdapter.getInstance()
                .userMe("Bearer " + accessToken, paramMap)
                .enqueue(super.handleResponse("res_cnt", "등록계좌수", responseJson -> {

                            // 성공하면 결과화면으로 이동
                            result = new Gson().fromJson(responseJson, ApiCallUserMeResponse.class);
                            args.putParcelable("result", result);

                            // 상단 고객정보
                            ((TextView) view.findViewById(R.id.tvUserNameInfo)).setText(String.format("%s(%s)", result.getUser_name(), result.getUser_seq_no()));

                            // 계좌정보(반복부)
                            recyclerView = view.findViewById(R.id.recyclerView);
                            recyclerView.setLayoutManager(new LinearLayoutManager(context));
                            recyclerView.addItemDecoration(new KmRecyclerViewDividerHeight(30));

                            view.findViewById(R.id.btnNext).setOnClickListener(v -> goNext());

                            initData();

                            // CenterAuthAPIUserMeResultFragment로 이동
                            // startFragment(CenterAuthAPIUserMeResultFragment.class, args, R.string.fragment_id_api_call_userme);
                        })
                );
    }

    void initData() {

        // 리사이클러뷰에 어댑터 설정
        adapter = new AccountListAdapter(result.getRes_list());
        recyclerView.setAdapter(adapter);

        // 다른 메뉴에서 계좌정보를 사용할 수 있도록 저장.
        if (adapter.getItemCount() > 0) {
            CenterAuthUtils.saveCenterAuthBankAccountList(adapter.getItemList());
            Toast.makeText(context, "계좌정보 저장됨", Toast.LENGTH_SHORT).show();
        }

        // CI 정보 저장.
        Utils.saveData(CenterAuthConst.CENTER_AUTH_USER_CI, result.getUser_ci());
    }

    void goNext() {
        // startFragment(CenterAuthAPIUserMeResultFragment.class, args, R.string.fragment_id_api_call_userme);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
