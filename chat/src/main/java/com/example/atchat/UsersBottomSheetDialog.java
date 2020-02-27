package com.example.atchat;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;

public class UsersBottomSheetDialog extends BottomSheetDialogFragment {

    private static final String TAG = UsersBottomSheetDialog.class.getSimpleName();
    private TextView userTextView;
    private boolean isViewCreated = false;
    private RecyclerView recyclerViewUsers;
    private UsersAdapter usersAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView called");
        View view = inflater.inflate(R.layout.users_bottom_sheet_layout, container, false);
        userTextView = view.findViewById(R.id.user_text_view);

        recyclerViewUsers = (RecyclerView) view.findViewById(R.id.users_recyclerview);
        LinearLayoutManager usersLayoutManager =
                new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false);
        recyclerViewUsers.setLayoutManager(usersLayoutManager);
        usersAdapter = new UsersAdapter();
        recyclerViewUsers.setAdapter(usersAdapter);
        isViewCreated = true;
        return view;
    }

    public void setUsers(List<User> users) {
        if (users != null && isViewCreated) {
          usersAdapter.setUsers(users);
        }
    }
}
