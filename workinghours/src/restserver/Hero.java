package restserver;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Hero
{
    @XmlElement(name = "id")
    private int m_id;
    @XmlElement(name = "name")
    private String m_name;

    public Hero(int id, String name)
    {
        m_id = id;
        m_name = name;
    }

    public int getId()
    {
        return m_id;
    }

    public String getName()
    {
        return m_name;
    }

    public void setId(int id)
    {
        m_id = id;
    }

    public void setName(String name)
    {
        m_name = name;
    }

    @Override
    public String toString()
    {
        return "{\"id\":\"" + m_id + "\",\"name\":\"" + m_name + "\"}";
    }
    
}
