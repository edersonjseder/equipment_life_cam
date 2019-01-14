package cam.equipment.life.com.equipmentlifecam.paid.viewholder;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.paid.listeners.OnItemClickListener;

public class EquipmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    @BindView(R.id.circleView_equipment_picture) ImageView circleViewEquipmentPicture;

    @BindView(R.id.image_view_equipment_list_status) ImageView imageViewEquipmentListStatus;

    @BindView(R.id.tv_equipment_brand_list_info) TextView textViewEquipmentBrand;

    @BindView(R.id.et_equipment_owner) TextView textViewEquipmentOwner;

    private OnItemClickListener onItemClickListener;

    public EquipmentViewHolder(@NonNull View itemView) {
        super(itemView);

        ButterKnife.bind(this, itemView);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        onItemClickListener.onItemClick(view, getAdapterPosition());
    }

    public void setOnItemClickListener(cam.equipment.life.com.equipmentlifecam.paid.listeners.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ImageView getCircleViewEquipmentPicture() {
        return circleViewEquipmentPicture;
    }

    public ImageView getImageViewEquipmentListStatus() {
        return imageViewEquipmentListStatus;
    }

    public TextView getTextViewEquipmentBrand() {
        return textViewEquipmentBrand;
    }

    public TextView getTextViewEquipmentOwner() {
        return textViewEquipmentOwner;
    }
}
