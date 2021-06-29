package 模拟;

public class LeetCode168 {
    /** 168. Excel表列名称
     * 很像进制又有所不同，没有0，取值是1-26，十进制取值是0-9，在n%26==0时，末尾补Z，然后a减1以抵消最后的Z
     */
    public String convertToTitle(int a) {
        String res="";
        int r;
        while(a!=0){
            if(a%26==0){
                res='Z'+res;
                a=a/26;
                // 例如a=26，直接当作Z使用就完事了，不需要a=a/26=1进行下一轮
                a--;
            }else{
                r=a%26;
                res=(char)('A'+r-1)+res;
                a=a/26;
            }
        }
        return res;
    }
}
