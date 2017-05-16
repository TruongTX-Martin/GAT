package com.gat.feature.personal.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gat.R;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Constance;
import com.gat.common.util.MZDebug;
import com.gat.common.util.Strings;
import com.gat.feature.bookdetailowner.BookDetailOwnerActivity;
import com.gat.feature.bookdetailsender.BookDetailSenderActivity;
import com.gat.feature.main.MainActivity;
import com.gat.repository.entity.book.BookRequestEntity;
import com.gat.feature.personal.fragment.FragmentBookRequest;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 07/04/2017.
 */

public class BookRequestAdapter extends RecyclerView.Adapter<BookRequestAdapter.BookRequestViewHolder> {

    private Context context;
    private List<BookRequestEntity> listBookRequest;
    private LayoutInflater inflate;
    private FragmentBookRequest fragment;
    public BookRequestAdapter(Context context, List<BookRequestEntity> list, FragmentBookRequest readingBook) {
        this.context = context;
        this.listBookRequest = list;
        this.fragment = readingBook;
        inflate = LayoutInflater.from(context);
    }

    @Override
    public BookRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflate.inflate(R.layout.layout_item_book_request,null);
        return new BookRequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookRequestViewHolder holder, int position) {
            BookRequestEntity entity = listBookRequest.get(position);
        MZDebug.w("BookRequest: " + entity.toString());
            if (entity != null) {
                if (entity.getRecordType() == 1) {
                    //yeu cau toi ban
                    if(entity.isHeader()){
                        holder.layoutTitle.setVisibility(View.VISIBLE);
                        holder.txtTopTitle.setText("Yêu cầu tới bạn");
                        holder.txtTopNumber.setText("("+fragment.getNumberRequestToYou()+")");
                    }else{
                        holder.layoutTitle.setVisibility(View.GONE);
                    }
                    if(!Strings.isNullOrEmpty(entity.getBorrowerImageId())) {
                        ClientUtils.setImage(context, holder.imgAvatar, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getBorrowerImageId(), Constance.IMAGE_SIZE_SMALL));
                    }
                    if(!Strings.isNullOrEmpty(entity.getBorrowerName())) {
                        holder.txtBorrowerName.setText(entity.getBorrowerName());
                    }
                    setTextStatus(holder.txtStatus,entity.getRecordStatus());
                    if(entity.getRecordStatus() == 0) {
                        holder.layoutConfirm.setVisibility(View.VISIBLE);
                    }else{
                        holder.layoutConfirm.setVisibility(View.GONE);
                    }
                    holder.txtAgreed.setOnClickListener(v -> fragment.agreedRequest(entity,position,true));
                    holder.txtReject.setOnClickListener(v -> fragment.rejectRequest(entity,position,false));
                    if(!Strings.isNullOrEmpty(entity.getEditionTitle())) {
                        holder.txtNameBook.setText(Html.fromHtml(ClientUtils.formatColor("Gửi yêu cầu mượn sách ","#000000") + ClientUtils.formatColor(entity.getEditionTitle(),"#5297ba")), TextView.BufferType.SPANNABLE);
                    }
                }else {
                    //yeu cau tu ban
                    if(entity.isHeader()){
                        holder.layoutTitle.setVisibility(View.VISIBLE);
                        holder.txtTopTitle.setText("Yêu cầu từ bạn");
                        holder.txtTopNumber.setText("("+fragment.getNumberRequestFromYou()+")");
                    }else{
                        holder.layoutTitle.setVisibility(View.GONE);
                    }
                    if(!Strings.isNullOrEmpty(entity.getOwnerImageId())) {
                        ClientUtils.setImage(context, holder.imgAvatar, R.drawable.ic_book_default, ClientUtils.getUrlImage(entity.getOwnerImageId(), Constance.IMAGE_SIZE_SMALL));
                    }
                    if(!Strings.isNullOrEmpty(entity.getOwnerName())) {
                        holder.txtBorrowerName.setText(entity.getOwnerName());
                    }
                    setTextStatus(holder.txtStatus,entity.getRecordStatus());
                    holder.layoutConfirm.setVisibility(View.GONE);
                    if(!Strings.isNullOrEmpty(entity.getEditionTitle())) {
                        holder.txtNameBook.setText(Html.fromHtml(ClientUtils.formatColor("Nhận yêu cầu mượn sách ","#000000") + ClientUtils.formatColor(entity.getEditionTitle(),"#5297ba") + ClientUtils.formatColor(" từ bạn","#000000")), TextView.BufferType.SPANNABLE);
                    }
                }
                holder.layoutClick.setOnClickListener(v -> {
                    int recordType = entity.getRecordType();
                    int recodeId = entity.getRecordId();
                    ClientUtils.showToast(this.context, "Type record:"+recordType);
                    if (recordType == 1) {
                        //sharing - user borrower -> request  by owner
                        Intent intent = new Intent(MainActivity.instance, BookDetailOwnerActivity.class);
                        intent.putExtra("BorrowingRecordId", recodeId);
                        MainActivity.instance.startActivity(intent);
                    } else if (recordType == 2 || recordType == 0) {
                        //borrowing - use owerner -> request by sender
                        Intent intent = new Intent(MainActivity.instance, BookDetailSenderActivity.class);
                        intent.putExtra("BorrowingRecordId", recodeId);
                        MainActivity.instance.startActivity(intent);
                    }
                });
            }
            if(getItemCount() > 9 && position == (getItemCount() -1)){
                fragment.loadMore();
            }
    }

    private void setTextStatus(TextView textStatus,int status){
        switch (status){
            case 0:
                textStatus.setText("Đợi đồng ý");
                break;
            case 1:
                textStatus.setText("Đợi đến lượt");
                break;
            case 2:
                textStatus.setText("Đang liên lạc");
                break;
            case 3:
                textStatus.setText("Đang mượn");
                break;
            case 4:
                textStatus.setText("Đã trả");
                break;
            case 5:
                textStatus.setText("Đã từ chối");
                break;
            case 6:
                textStatus.setText("Đã huỷ");
                break;
            case 7:
                textStatus.setText("Không trả sách");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listBookRequest.size();
    }


    public class  BookRequestViewHolder extends RecyclerView.ViewHolder{
        TextView txtBorrowerName,txtSendRequest,txtNameBook,txtAgreed,txtReject,txtStatus;
        CircleImageView imgAvatar;
        LinearLayout layoutTitle,layoutConfirm;
        TextView txtTopTitle,txtTopNumber;
        RelativeLayout layoutClick;
        public BookRequestViewHolder(View itemView) {
            super(itemView);
            txtBorrowerName = (TextView) itemView.findViewById(R.id.txtBorrowerName);
            txtSendRequest = (TextView) itemView.findViewById(R.id.txtSendRequest);
            txtNameBook = (TextView) itemView.findViewById(R.id.txtNameBook);
            txtAgreed = (TextView) itemView.findViewById(R.id.txtAgreed);
            txtReject = (TextView) itemView.findViewById(R.id.txtReject);
            txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);

            imgAvatar = (CircleImageView) itemView.findViewById(R.id.imgAvatar);
            layoutTitle = (LinearLayout) itemView.findViewById(R.id.layoutTitle);
            layoutConfirm = (LinearLayout) itemView.findViewById(R.id.layoutConfirm);
            txtTopTitle = (TextView) itemView.findViewById(R.id.txtTopTitle);
            txtTopNumber = (TextView) itemView.findViewById(R.id.txtTopNumber);
            layoutClick = (RelativeLayout) itemView.findViewById(R.id.layoutClick);
        }
    }
}
