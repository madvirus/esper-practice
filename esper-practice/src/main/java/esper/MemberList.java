package esper;

public class MemberList extends PageVisit {

    public MemberList(String name, String userId) {
        super(name, userId, "/member/list");
    }

    @Override
    public String toString() {
        return "MemberList[name="+getName()+",userId=" + getUserId() + "]";
    }
}
