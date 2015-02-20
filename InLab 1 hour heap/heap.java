public class heap{
	int[] heap = new int[10];
	
	public void add_in_heap_order(int number_to_add){
		for(int i = 1; i<10; i++){
			if(heap[i] == 0){
				heap[i] = number_to_add;
				bubble_up(i);
				break;
			}
		}
	}
	
	public void bubble_up(int index){
		if(heap[index] < heap[index/2]){
			int temp = heap[index];
			heap[index] = heap[index/2];
			heap[index/2] = temp;
			bubble_up(index/2);
		}
	}
	
	public void bubble_down(int index){
		if(index > 4){
			return;
		}
		int minChild = (heap[index*2] < heap[index*2+1]) ? heap[index*2] : heap[index*2+1];
		int mindex = (heap[index*2] < heap[index*2+1]) ? index*2 : index*2+1;
		if(minChild == 0 && mindex == index*2+1){
			minChild = heap[index*2];
			mindex = index*2;
			if(minChild == 0) return;
		}
		if(minChild < heap[index]){
			int temp = heap[index];
			heap[index] = heap[mindex];
			heap[mindex] = temp;
			bubble_down(mindex);
		}
	}
	
	public int remove_min(){
		int minimum = heap[1];
		for(int i = 1; i<10; i++){
			if(heap[i] == 0){
				heap[1] = heap[i-1];
				heap[i-1] = 0;
				bubble_down(1);
				break;
			}else if(i == 9){
				heap[1] = heap[i];
				heap[i] = 0;
				bubble_down(1);
				break;
			}
		}
		return minimum;
	}
	
	public void check_element(int index){
		bubble_down(index);
		bubble_up(index);
	}
	
	public static void main(String[] args){
		heap myHeap = new heap();
		myHeap.add_in_heap_order(3);
		myHeap.add_in_heap_order(9);
		myHeap.add_in_heap_order(8);
		myHeap.add_in_heap_order(2);
		myHeap.add_in_heap_order(5);
		myHeap.add_in_heap_order(7);
		myHeap.add_in_heap_order(4);
		myHeap.add_in_heap_order(6);
		myHeap.add_in_heap_order(1);
		myHeap.remove_min();
		myHeap.remove_min();
		myHeap.heap[4] = 2;
		myHeap.check_element(4);
		for(int i = 1; i<10; i++){
			System.out.println(myHeap.heap[i]);
		}
	}
}

//remove min twice,
//change 6 to 2 and rebalance