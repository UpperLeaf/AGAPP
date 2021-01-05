package com.wonsang.agapp.fragment;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wonsang.agapp.R;
import com.wonsang.agapp.dialog.AddressDialog;
import com.wonsang.agapp.dialog.ContactDialog;
import com.wonsang.agapp.model.UserModel;
import com.wonsang.agapp.utils.ContentInfoProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AddressFragment extends Fragment {

    private EditText editText;
    private Button contactAddButton;
    private RecyclerView recyclerView;
    private ContentInfoProvider contentInfoProvider;
    private UserModel userModel;
    private AddressAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.address_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.address_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        contentInfoProvider = new ContentInfoProvider();
        adapter = new AddressAdapter(getContext(), getAllUsers());
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));


        contactAddButton = view.findViewById(R.id.submit);
        contactAddButton.setOnClickListener((View v) -> {
            ContactDialog dialog = new ContactDialog(getContext(), contentInfoProvider);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setGravity(Gravity.CENTER);
            dialog.show();
            dialog.setOnDismissListener(dialog1 -> {
                if (dialog.isFetched())
                    notifyDataChanged();
            });
        });
        editText = view.findViewById(R.id.search_name);
        editText.setOnKeyListener((v, keyCode, event) -> {
            if(event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER){
                EditText text = (EditText)v;
                String name = text.getText().toString();
                int position = adapter.searchPositionByName(name);
                if(position != -1)
                    recyclerView.smoothScrollToPosition(position);
                return true;
            }
            return false;
        });

    }

    public void notifyDataChanged() {
        adapter.users = getAllUsers();
        adapter.notifyDataSetChanged();
    }

    private List<UserModel> getAllUsers() {
        return contentInfoProvider.getAllUsers(Objects.requireNonNull(getContext()).getContentResolver());
    }

    class AddressAdapter extends RecyclerView.Adapter<AddressViewHolder> {
        private List<AddressViewHolder> addressViewHolders;
        private List<UserModel> users;
        private Context context;

        AddressAdapter(Context context, List<UserModel> users){
            this.addressViewHolders = new ArrayList<>();
            this.users = users;
            this.context = context;
        }

        @NonNull
        @Override
        public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.address_card, parent, false);
            AddressViewHolder viewHolder = new AddressViewHolder(view);
            addressViewHolders.add(viewHolder);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
            UserModel user = users.get(position);
            contentInfoProvider = new ContentInfoProvider();

            holder.getName().setText(user.getName());
            holder.getPhoneNumber().setText(user.getPhoneNumber().get(0));
            Glide.with(context)
                    .load(user.getPhotoUri())
                    .placeholder(R.drawable.blank_person)
                    .into(holder.getImageView());
            holder.addressView.setOnClickListener(v -> {
                AddressDialog dialog = new AddressDialog(getContext(), user, contentInfoProvider);
                dialog.setCancelable(true);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getWindow().setGravity(Gravity.CENTER);
                dialog.show();
                dialog.setOnDismissListener(dialog1 -> {
                    if (dialog.isFetched())
                        notifyDataChanged();
                });
            });

        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        public int searchPositionByName(String name) {
            int position = -1;
            for(int i = 0; i < addressViewHolders.size(); i++){
                if(addressViewHolders.get(i).getUserName().contains(name)){
                    position = i;
                    break;
                }
            }
            return position;
        }
    }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final TextView phoneNumber;
        private final ImageView imageView;

        private final CardView addressView;

        public AddressViewHolder(@NonNull View view) {
            super(view);
            this.name = view.findViewById(R.id.name);
            this.phoneNumber = view.findViewById(R.id.phoneNumber);
            this.imageView = view.findViewById(R.id.contact_image_view);
            this.addressView = view.findViewById(R.id.address_card_view);
        }

        public TextView getName() {
            return name;
        }
        public TextView getPhoneNumber() {
            return phoneNumber;
        }
        public ImageView getImageView() {
            return imageView;
        }

        public String getUserName() {
            return name.getText().toString();
        }
    }

    static class DividerItemDecoration extends RecyclerView.ItemDecoration {

        private static final int[] ATTRS = new int[]{
                android.R.attr.listDivider
        };

        public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

        public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;

        private Drawable mDivider;

        private int mOrientation;

        public DividerItemDecoration(Context context, int orientation) {
            final TypedArray a = context.obtainStyledAttributes(ATTRS);
            mDivider = a.getDrawable(0);
            a.recycle();
            setOrientation(orientation);
        }

        public void setOrientation(int orientation) {
            if (orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST) {
                throw new IllegalArgumentException("invalid orientation");
            }
            mOrientation = orientation;
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                drawVertical(c, parent);
            } else {
                drawHorizontal(c, parent);
            }
        }

        public void drawVertical(Canvas c, RecyclerView parent) {
            final int left = parent.getPaddingLeft();
            final int right = parent.getWidth() - parent.getPaddingRight();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int top = child.getBottom() + params.bottomMargin;
                final int bottom = top + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        public void drawHorizontal(Canvas c, RecyclerView parent) {
            final int top = parent.getPaddingTop();
            final int bottom = parent.getHeight() - parent.getPaddingBottom();

            final int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                final View child = parent.getChildAt(i);
                final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                        .getLayoutParams();
                final int left = child.getRight() + params.rightMargin;
                final int right = left + mDivider.getIntrinsicHeight();
                mDivider.setBounds(left, top, right, bottom);
                mDivider.draw(c);
            }
        }

        @Override
        public void getItemOffsets(Rect outRect, int itemPosition, RecyclerView parent) {
            if (mOrientation == VERTICAL_LIST) {
                outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
            } else {
                outRect.set(0, 0, mDivider.getIntrinsicWidth(), 0);
            }
        }
    }
}

