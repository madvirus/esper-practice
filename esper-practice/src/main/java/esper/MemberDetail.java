package esper;

public class MemberDetail extends PageVisit {
    public MemberDetail(String name, String userId) {
        super(name, userId, "/member/detail");
    }

    @Override
    public String toString() {
        return "MemberDetail[name="+getName()+",userId=" + getUserId() + "]";
    }
}
