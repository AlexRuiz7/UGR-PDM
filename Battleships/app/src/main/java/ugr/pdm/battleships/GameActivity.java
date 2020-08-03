package ugr.pdm.battleships;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import ugr.pdm.battleships.adapters.GridAdapter;
import ugr.pdm.battleships.adapters.ItemOffsetDecoration;
import ugr.pdm.battleships.models.GridCell;

public class GameActivity extends AppCompatActivity {

//    private GridView myFleetGrid, enemyFleetGrid;
    private RecyclerView myFleetGrid, enemyFleetGrid;

    private ArrayList<GridCell> mCells1;
    private ArrayList<GridCell> mCells2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();
    }

    private void initViews() {
        myFleetGrid = findViewById(R.id.myFleetGridView);
        enemyFleetGrid = findViewById(R.id.enemyFleetGridView);

//        myFleetGrid.setNumColumns(9);
//        enemyFleetGrid.setNumColumns(9);

        mCells1 = new ArrayList<>();
        mCells2 = new ArrayList<>();
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                mCells1.add(new GridCell(i, j));
                mCells2.add(new GridCell(i, j));
            }
        }


        myFleetGrid.setLayoutManager(new GridLayoutManager(this, 9));
        enemyFleetGrid.setLayoutManager(new GridLayoutManager(this, 9));

        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(this, R.dimen.item_offet);
        myFleetGrid.addItemDecoration(itemDecoration);
        enemyFleetGrid.addItemDecoration(itemDecoration);

        GridAdapter myAdapter = new GridAdapter(mCells1,this);
        GridAdapter enemyAdapter = new GridAdapter(mCells2, this);

        myFleetGrid.setAdapter(myAdapter);
        enemyFleetGrid.setAdapter(enemyAdapter);

        myAdapter.notifyDataSetChanged();
        enemyAdapter.notifyDataSetChanged();
    }


}

