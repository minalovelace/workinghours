package restserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Heroes
{
    @XmlElement(name = "data")
    private List<Hero> m_heroes = null;
    private static Heroes singleton;

    private Heroes()
    {
        m_heroes = new ArrayList<Hero>(10);
        Hero[] myHeroes =
        { new Hero(11, "Mr. Nice"), new Hero(12, "Narco"), new Hero(13, "Bombasto"), new Hero(14, "Celeritas"),
                new Hero(15, "Magneta"), new Hero(16, "RubberMan"), new Hero(17, "Dynama"), new Hero(18, "Dr IQ"),
                new Hero(19, "Magma"), new Hero(20, "Tornado") };
        m_heroes.addAll(Arrays.asList(myHeroes));
    }

    public static Heroes getSingleton()
    {
        if (singleton == null)
            singleton = new Heroes();
        return singleton;
    }

    public void addHero(Hero hero)
    {
        if (hero != null)
        {
            m_heroes.add(hero);
        } else
        {
            m_heroes = new ArrayList<>(1);
            m_heroes.add(hero);
        }
    }

    public void deleteHero(Hero hero)
    {
        if (m_heroes.contains(hero))
            m_heroes.remove(hero);
    }

    public List<Hero> getHeroes()
    {
        return m_heroes;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\":[");
        sb.append(m_heroes.stream().map(hero -> hero.toString()).collect(Collectors.joining(",")));
        sb.append("]}");
        return sb.toString();
    }

}
