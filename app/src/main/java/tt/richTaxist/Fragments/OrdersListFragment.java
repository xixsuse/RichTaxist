package tt.richTaxist.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;
import java.util.ArrayList;
import tt.richTaxist.Adapters.RecyclerViewOrderAdapter;
import tt.richTaxist.Bricks.CustomSpinner;
import tt.richTaxist.Bricks.CustomSpinner.TypeOfSpinner;
import tt.richTaxist.Bricks.SingleChoiceListDF;
import tt.richTaxist.Constants;
import tt.richTaxist.DB.DataSource;
import tt.richTaxist.R;
import tt.richTaxist.Units.Order;

public class OrdersListFragment extends Fragment implements
        SingleChoiceListDF.SingleChoiceListDFInterface {
    public static final String TAG = "OrdersListFragment";
    private OrdersListInterface listener;
    private RecyclerViewOrderAdapter rvAdapter;
    private CustomSpinner spnTaxopark;
    private DataSource dataSource;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataSource = new DataSource(context.getApplicationContext());
        try { listener = (OrdersListInterface) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OrdersListInterface");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_orders_list, container, false);
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        spnTaxopark = (CustomSpinner) rootView.findViewById(R.id.spnTaxopark);

        ArrayList<Order> ordersList = dataSource.getOrdersSource().getOrdersList(listener.getCurrentShiftId(), spnTaxopark.getSelectedItemId());
        rvAdapter = new RecyclerViewOrderAdapter(ordersList);
        recyclerView.setAdapter(rvAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        rvAdapter.setListener(new RecyclerViewOrderAdapter.Listener() {
            @Override
            public void onClick(Object selectedObject) {
                Order selectedOrder = (Order) selectedObject;
                Toast.makeText(getContext(), R.string.orderSelectedMSG, Toast.LENGTH_SHORT).show();
                listener.returnToOrderFragment(selectedOrder);
            }

            @Override
            public void onClickMore(Object selectedObject, int positionInRVList) {
                Order selectedOrder = (Order) selectedObject;
                SingleChoiceListDF dialog = new SingleChoiceListDF();
                Bundle args = new Bundle();
                args.putLong(Constants.OBJECT_ID_EXTRA, selectedOrder.orderID);
                args.putInt(Constants.POSITION_EXTRA, positionInRVList);
                dialog.setArguments(args);
                dialog.show(getChildFragmentManager(), "SingleChoiceListDF");
            }
        });

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        createTaxoparkSpinner();
    }

    public void createTaxoparkSpinner(){
        spnTaxopark.createSpinner(TypeOfSpinner.TAXOPARK, true);
        spnTaxopark.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View itemSelected, int selectedItemPosition, long selectedId) {
                spnTaxopark.saveSpinner(TypeOfSpinner.TAXOPARK);
                rvAdapter.setOrders(dataSource.getOrdersSource().getOrdersList(listener.getCurrentShiftId(), spnTaxopark.taxoparkID));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {/*NOP*/}
        });
    }

    public void addOrderToList(Order order) {
        if (rvAdapter != null) {
            rvAdapter.addOrderToList(order);
        }
    }

    @Override
    public void processListItem(long selectedOrderID, int selectedActionID, int positionInRVList) {
        Order selectedOrder = dataSource.getOrdersSource().getOrderByID(selectedOrderID);

        switch (selectedActionID){
            case 0://править
                listener.returnToOrderFragment(selectedOrder);
                break;

            case 1://показать подробности
                Toast.makeText(getContext(), selectedOrder.getDescription(getContext(), dataSource), Toast.LENGTH_LONG).show();
                break;

            case 2://удалить
                dataSource.getOrdersSource().remove(selectedOrder);
                rvAdapter.removeOrderFromList(selectedOrder, positionInRVList);
                Toast.makeText(getContext(), R.string.orderDeletedMSG, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public interface OrdersListInterface {
        void returnToOrderFragment(Order selectedOrder);
        long getCurrentShiftId();
    }
}
