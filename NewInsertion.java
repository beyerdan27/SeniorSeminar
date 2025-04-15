import java.util.ArrayList;
public class NewInsertion {
    ArrayList<Integer> arr;
    public NewInsertion(){
        //do nothing
    }
    public ArrayList<Integer> insertionSort(ArrayList<Integer> arr){
        this.arr = arr;
        int size = arr.size();
        for(int i=0; i<size; i++){
            int j=i;
            while(j>0&&this.arr.get(j-1)>this.arr.get(j)){
                swap(j, j-1);
                j-=1;
            }
        }
        return this.arr;
    }
    public void swap(int inda, int indb){
        int temp = arr.get(inda);
        arr.set(inda, arr.get(indb));
        arr.set(indb, temp);
    }
}
