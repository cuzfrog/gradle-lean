import com.google.common.base.Preconditions;

class TestApp{
    public static void main(String[] args){
        Preconditions.checkArgument(args.length > 1);
    }
}