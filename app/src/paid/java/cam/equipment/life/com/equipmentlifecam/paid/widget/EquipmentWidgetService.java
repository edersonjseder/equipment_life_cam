package cam.equipment.life.com.equipmentlifecam.paid.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class EquipmentWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new EquipmentWidgetDataProvider(this.getApplicationContext(), intent));
    }
}
