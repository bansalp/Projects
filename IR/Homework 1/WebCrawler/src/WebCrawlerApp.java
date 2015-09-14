import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WebCrawlerApp 
{
	static LinkedList<LinkNode> frontier = new LinkedList<LinkNode>();
	static LinkedList<LinkNode> seen = new LinkedList<LinkNode>();
	static String prefix = "https://en.wikipedia.org/wiki/";
	static String mainPage = "https://en.wikipedia.org/wiki/Main_Page";
	static String colon = ":";
	static int threshold = 1000;
	static int maxDepth = 5;
	
	public static void main(String[] args) 
	{
		try 
		{
			crawl("https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher", "");
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void crawl(String seed, String keyphrase) throws IOException, InterruptedException
	{
		LinkNode seedNode = new LinkNode(seed, 0);
		frontier.add(seedNode);
		
		while (!frontier.isEmpty())
		{
			LinkNode node = frontier.getFirst();
			
			if (node.getDepth() < maxDepth && seen.size() < threshold)
			{
				GetDocumentLinks(node);
				
				System.out.println("Frontier: " + frontier.size() + " Seen: " + seen.size() + " Depth: " + node.getDepth() + " Link: " + node.getLink());
				
				frontier.removeFirst();
				seen.add(node);
			}
			else
			{
				break;
			}
		}
		
		PrintWriter writer = new PrintWriter("URLs.txt", "UTF-8");		
		for (LinkNode link: seen)
		{
			writer.println(link.getLink());
		}
		writer.close();
	}
	
	public static void GetDocumentLinks(LinkNode node) throws IOException, InterruptedException
	{
		if (frontier.size() <= threshold)
		{
			Document doc = Jsoup.connect(node.getLink()).get();
			Elements links = doc.select("a[href]");
			FilterLinks(links, node);
			//Thread.sleep(1000);
		}
	}
	
	public static void FilterLinks(Elements links, LinkNode node)
	{
		for (Element link: links)
		{
			String absUrl = link.absUrl("href");
			
			if (IsEnglishPrefixLink(absUrl))
			{
				if (!ContainsColon(absUrl))
				{
					if (!IsMainPageLink(absUrl))
					{
						int newNodeDepth = node.getDepth() + 1;
						LinkNode newNode = new LinkNode(absUrl, newNodeDepth);
						
						if ((!frontier.contains(newNode)) && (!seen.contains(newNode)))
						{
							frontier.add(newNode);
						}
					}
				}
			}
		}
	}
	
	public static Boolean IsEnglishPrefixLink(String link)
	{
		return link.toLowerCase().contains(prefix.toLowerCase()); 
	}
	
	public static Boolean ContainsColon(String link)
	{
		return link.substring(prefix.length()).contains(colon);
	}
	
	public static Boolean IsMainPageLink(String link)
	{
		return link.toLowerCase().contains(mainPage.toLowerCase());
	}
}