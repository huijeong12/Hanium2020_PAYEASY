package com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.AbstractCenterAuthMainFragment;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.buyer.account_list.AccountListFragment;
import com.kftc.openbankingsample2.biz.center_auth.http.CenterAuthApiRetrofitAdapter;
import com.kftc.openbankingsample2.biz.center_auth.util.CenterAuthUtils;
import com.kftc.openbankingsample2.common.data.ApiCallUserMeResponse;
import com.kftc.openbankingsample2.common.util.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class TransferWithdrawResult extends AbstractCenterAuthMainFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;
    private String[] info;

    private String price;
    private String newPriceSpring;
    int newPriceInt;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        args = getArguments();
        if (args == null) args = new Bundle();

        info = args.getStringArray("trans_info");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_transfer_withdraw_result, container, false);
        callAPI();
        return view;
    }

    void callAPI() {

        // 은행거래고유번호
        String clientUseCode = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USE_CODE);
        String randomUnique9String = Utils.getCurrentTime();    // 이용기관 부여번호를 임시로 시간데이터 사용
        String etBankTranId = String.format("%sU%s", clientUseCode, randomUnique9String);

        // 거래 일시
        String etTranDtime = new SimpleDateFormat("yyyyMMddHHmmss", Locale.KOREA).format(new Date());

        // access_token
        String accessToken =  CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_ACCESS_TOKEN);
        Utils.saveData(CenterAuthConst.CENTER_AUTH_ACCESS_TOKEN, accessToken);

        // 핀테크 이용번호 (계좌번호)
        String fintechUseNum = info[4];
        Utils.saveData(CenterAuthConst.CENTER_AUTH_FINTECH_USE_NUM, fintechUseNum);


        String userSeqNo = CenterAuthUtils.getSavedValueFromSetting(CenterAuthConst.CENTER_AUTH_CLIENT_USER_SEQ_NUM);

        // 요청전문
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("bank_tran_id", etBankTranId);
        paramMap.put("fintech_use_num", fintechUseNum);
        paramMap.put("tran_dtime", etTranDtime);

        showProgress();
        CenterAuthApiRetrofitAdapter.getInstance()
                .accountBalanceFinNum("Bearer " + accessToken, paramMap)
                .enqueue(super.handleResponse("balance_amt", "잔액", responseJson -> {
                    initView();
                }));
    }

    void initView() {

        // 가게 이름
        EditText etRecvMarketName = view.findViewById(R.id.recv_client_market_name);
        etRecvMarketName.setText(info[0]);

        // 수취인 이름
        EditText etRecvClntName = view.findViewById(R.id.recv_client_name);
        etRecvClntName.setText(info[1]);

        // 수취인 계좌번호
        EditText etRecvClientAccountNum = view.findViewById(R.id.recv_client_account_num);
        etRecvClientAccountNum.setText(info[2]);

        // 거래 금액
        EditText etTranAmt = view.findViewById(R.id.trans_amt);
        etTranAmt.setText(info[3]);

        FirebaseDatabase database =FirebaseDatabase.getInstance();
        DatabaseReference myRef=database.getReference("market_info");

        price = info[3];
        Log.d("1. price: ", price);

        myRef.child("hanium2020").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                TextView money =view.findViewById(R.id.moneyInfo);
                String sales ="0";

                if(datasnapshot.child("Sales").getValue() != null){
                    sales =datasnapshot.child("Sales").getValue().toString();
                }

                Log.d("sales: ", sales);
                int salesInt = Integer.parseInt(sales);
                Log.d("2. salesInt", Integer.toString(salesInt));
                int priceInt = Integer.parseInt(info[3]);
                Log.d("3. priceInt", " "+salesInt);
                int newPrice = salesInt+priceInt;
                price = Integer.toString(newPrice);
                Log.d("4. newPrice: ", price);

                myRef.child("hanium2020").child("Sales").setValue(price);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("pay-easy", "Failed to read value.", error.toException());
            }
        });


        //myRef.child("hanium2020").child("Sales").setValue(price);

        view.findViewById(R.id.btnNext).setOnClickListener(v -> startFragment(AccountListFragment.class, args, R.string.fragment_id_account_list));
    }

}
