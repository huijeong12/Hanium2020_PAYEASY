package com.kftc.openbankingsample2.common.navi;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kftc.openbankingsample2.R;
// import com.kftc.openbankingsample2.biz.center_auth.api.inquiry_realname.CenterAuthAPIInquiryRealNameFragment;


public class NaviAPICallFragment extends AbstractNaviFragment {

    // context
    private Context context;

    // view
    private View view;

    // data
    private Bundle args;

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
        view = inflater.inflate(R.layout.fragment_navi_child_apicall, container, false);
        initView();
        return view;
    }

    void initView() {


//
//        // 계좌실명조회
//        view.findViewById(R.id.llRealName).setOnClickListener(v -> {
//            activity.closeNavi();
//            activity.startFragment(CenterAuthAPIInquiryRealNameFragment.class, args, R.string.fragment_id_api_call_realname);
//        });

        // 춭금이체

    }
}
