import java.util.*;

class RecordSurnameComparator implements Comparator<Record>
{
    @Override
    public int compare(Record o1, Record o2) {
        return o1.getSurname().compareTo(o2.getSurname());
    }
}

public class Record {
    private String recordBookNumber, surname, termNumber, subject, mark;

    public Record() {
    }

    public Record(Record r)
    {
        recordBookNumber = r.getRecordBookNumber();
        surname = r.getSurname();
        termNumber = r.getTermNumber();
        subject = r.getSubject();
        mark = r.getMark();
    }

    public Record(String bookNumber, String surname, String termNumber, String subject, String mark) {
        this.recordBookNumber = bookNumber;
        this.surname = surname;
        this.termNumber = termNumber;
        this.subject = subject;
        this.mark = mark;
    }

    public String getRecordBookNumber() {
        return recordBookNumber;
    }

    public String getSurname() {
        return surname;
    }

    public String getTermNumber() {
        return termNumber;
    }

    public String getSubject() {
        return subject;
    }

    public String getMark() {
        return mark;
    }

    public void setRecordBookNumber(String number) {
        this.recordBookNumber = number;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setTermNumber(String term) {
        this.termNumber = term;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public String toString()
    {
        return getRecordBookNumber() + " " + getSurname() + " " + getTermNumber() + " " + getSubject() + " " + getMark();
    }
}