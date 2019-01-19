package cam.equipment.life.com.equipmentlifecam.paid.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.List;

import cam.equipment.life.com.equipmentlifecam.R;
import cam.equipment.life.com.equipmentlifecam.model.Equipment;
import cam.equipment.life.com.equipmentlifecam.paid.listeners.OnEquipmentItemSelectedListener;
import cam.equipment.life.com.equipmentlifecam.paid.listeners.OnItemClickListener;
import cam.equipment.life.com.equipmentlifecam.paid.viewholder.EquipmentViewHolder;
import cam.equipment.life.com.equipmentlifecam.utils.Utils;

public class EquipmentAdapter extends RecyclerView.Adapter<EquipmentViewHolder> {

    private static final String TAG = EquipmentAdapter.class.getSimpleName();

    private OnEquipmentItemSelectedListener mOnEquipmentItemSelectedListener;
    private List<Equipment> equipmentList;
    private Context context;

    public EquipmentAdapter(OnEquipmentItemSelectedListener mOnEquipmentItemSelectedListener,
                            Context mContext) {

        this.context = mContext;
        this.mOnEquipmentItemSelectedListener = mOnEquipmentItemSelectedListener;

    }

    @NonNull
    @Override
    public EquipmentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.card_view_equipment_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);

        return new EquipmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EquipmentViewHolder equipmentViewHolder, int position) {

        final Equipment equipment = equipmentList.get(position);

        equipmentViewHolder.getTextViewEquipmentBrand().setText(equipment.getBrand());
        equipmentViewHolder.getTextViewEquipmentOwner().setText(equipment.getOwner());

        if (equipment.getStatus().equals(context.getResources().getString(R.string.equipment_owned))) {
            equipmentViewHolder.getImageViewEquipmentListStatus().setImageDrawable(context.getDrawable(R.drawable.icon_ok));

        } else if (equipment.getStatus().equals(context.getResources().getString(R.string.equipment_sold))) {
            equipmentViewHolder.getImageViewEquipmentListStatus().setImageDrawable(context.getDrawable(R.drawable.icon_info));

        } else if (equipment.getStatus().equals(context.getResources().getString(R.string.equipment_stolen))) {
            equipmentViewHolder.getImageViewEquipmentListStatus().setImageDrawable(context.getDrawable(R.drawable.icon_not_ok));
        }

        Uri imageUrl = null;

        if ((equipment.getPicture() != null)) {

            if ((!equipment.getPicture().isEmpty()) || (equipment.getPicture() != "")) {

                imageUrl = Utils.buildImageUrl(equipment.getPicture());
            }

        }

        if ((imageUrl != null)) {

            Glide.with(context)
                    .load(imageUrl)
                    .into(equipmentViewHolder.getCircleViewEquipmentPicture());


        } else {

            equipmentViewHolder.getCircleViewEquipmentPicture().setImageDrawable(ContextCompat.getDrawable(context, R.drawable.album_icon));

        }


        equipmentViewHolder.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                mOnEquipmentItemSelectedListener.onEquipmentItemSelected(equipment, position);
            }
        });

    }

    /**
     * When data changes, this method updates the list of equipments
     * and notifies the adapter to use the new values on it
     */
    public void setEquipments(List<Equipment> equipments) {
        equipmentList = equipments;
        notifyDataSetChanged();
    }

    public List<Equipment> getEquipmentList() {
        return equipmentList;
    }

    @Override
    public int getItemCount() {
        return (equipmentList != null) ? equipmentList.size() : 0;
    }

}
