package com.kftc.openbankingsample2.biz.center_auth.buyer.account_list;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.zxing.integration.android.IntentIntegrator;
import com.kftc.openbankingsample2.R;
import com.kftc.openbankingsample2.biz.center_auth.CenterAuthConst;
import com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw.TransferWithdrawCheck;
import com.kftc.openbankingsample2.biz.center_auth.buyer.transfer_withdraw.TransferWithdrawScanQR;
import com.kftc.openbankingsample2.biz.main.MainActivity;
import com.kftc.openbankingsample2.common.data.BankAccount;
import com.kftc.openbankingsample2.common.util.view.KmDialogDefault;
import com.kftc.openbankingsample2.common.util.view.recyclerview.KmRecyclerViewArrayAdapter;
import com.kftc.openbankingsample2.common.util.view.recyclerview.KmRecyclerViewHolder;

import java.util.ArrayList;

import timber.log.Timber;

public class AccountListAdapter extends KmRecyclerViewArrayAdapter<BankAccount> {

    protected MainActivity activity;
    Context context;

    public AccountListAdapter(ArrayList<BankAccount> itemList) {
        super(itemList);
    }

    @NonNull
    @Override
    public KmRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        activity = (MainActivity) context;

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_account_list, parent, false);
        return new ApiCallUserMeResultViewHolder(v, this);
    }

    public class ApiCallUserMeResultViewHolder extends KmRecyclerViewHolder<BankAccount> {

        private AccountListAdapter adapter;

        public ApiCallUserMeResultViewHolder(View v, AccountListAdapter adapter) {
            super(v);
            this.adapter = adapter;
        }

        @Override
        public void onBindViewHolder(View view, int position, BankAccount item, boolean isSelected, boolean isExpanded, boolean isEtc) {
            view.findViewById(R.id.btnWithdraw).setOnClickListener(v -> {
                Bundle args;
                args = new Bundle();

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("결제");
                builder.setMessage(item.getBank_name() + " 계좌로 결제 진행하시겠습니까?");

                builder.setPositiveButton("결제",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] strings = { item.getAccount_type(), item.getAccount_num(), item.getFintech_use_num(),
                                    item.getAccount_holder_name(), item.getFintech_use_num(), item.getPayer_num(), "TR"};
                                args.putStringArray("buyer_info", strings);
                                startFragment(TransferWithdrawScanQR.class, args, R.string.fragment_id_transfer_scan_qr);
                            }
                        });

                builder.setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                builder.show();
            });

            ((TextView) view.findViewById(R.id.tvFintechUseNum)).setText(item.getFintech_use_num());
            ((TextView) view.findViewById(R.id.tvBankInfo)).setText(String.format("%s(%s)", item.getBank_name(), item.getBank_code_std()));

            // 계좌정보
            ((TextView) view.findViewById(R.id.tvAccountInfo)).setText(String.format("%s  %s", item.getAccountNum(), item.getAccount_holder_name()));

            // 조회, 출금 동의
            ((TextView) view.findViewById(R.id.tvAgree)).setText(String.format("조회: %s, 출금: %s", item.isInquiry_agree_yn() ? "동의" : "미동의", item.isTransfer_agree_yn() ? "동의" : "미동의"));

            // 등록계좌정보
            if (!item.getAccount_state().isEmpty()) {
                view.findViewById(R.id.trPayerNum).setVisibility(View.GONE);
                view.findViewById(R.id.trAccountState).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.tvAccountState)).setText(item.getAccountState());
            }

            // 사용자정보조회 : 납부자번호(추가)
            else {
                view.findViewById(R.id.trPayerNum).setVisibility(View.VISIBLE);
                view.findViewById(R.id.trAccountState).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.tvPayerNum)).setText(item.getPayer_num());
            }
        }

        // fragment 시작
        public void startFragment(Class fragmentClass, Bundle args, @StringRes int tagResId) {
            activity.startFragment(fragmentClass, args, tagResId);
        }

        // fragment 시작
        public void startFragment(Class fragmentClass, Bundle args, String TAG_FRAGMENT, boolean replace, boolean keep) {
            activity.startFragment(fragmentClass, args, TAG_FRAGMENT, replace, keep);
        }
    }
}
