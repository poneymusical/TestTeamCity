namespace TestTeamCity.ClassLib.Tests;

public class UnitTest1
{
    [Fact]
    public void Add()
    {
        Assert.Equal(4, Class1.Add(2, 2));
    }
}