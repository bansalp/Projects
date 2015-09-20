import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

public class WebCrawlerApp 
{
	static LinkedList<LinkNode> frontier = new LinkedList<LinkNode>();
	static LinkedList<LinkNode> seen = new LinkedList<LinkNode>();
	
	static String seedPage = "https://en.wikipedia.org/wiki/Hugh_of_Saint-Cher";
	static String keyPhrase = "index";
	
	static String prefix = "https://en.wikipedia.org/wiki/";
	static String mainPage = "https://en.wikipedia.org/wiki/Main_Page";
	
	static String fileName = "URLs.txt";
	static String charSet = "UTF-8";
	
	static String colon = ":";
	static String pound = "#";
	
	static String parseHREFNodes = "a[href]";
	static String parseHREF = "href";
	
	static int threshold = 1000;
	
	static int seedDepth = 1;
	static int maxDepth = 5;
	
	public static void main(String[] args) 
	{
		try 
		{
			crawl();
		} 
		catch (Exception e) 
		{
			System.out.println(e.getMessage());
		}
	}
	
	public static void crawl() throws IOException, InterruptedException
	{
		LinkNode seedNode = new LinkNode(seedPage, seedDepth);
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
		
		WriteURLsToFile();
	}
	
	public static void WriteURLsToFile() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(fileName, charSet);		
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
			Elements links = doc.select(parseHREFNodes);
			FilterLinks(links, node);
			Thread.sleep(1000);
		}
	}
	
	public static void FilterLinks(Elements links, LinkNode node)
	{
		for (Element link: links)
		{
			String absUrl = link.absUrl(parseHREF);
			
			if (ContainsPound(absUrl))
			{
				absUrl = absUrl.split(pound)[0];
			}
			
			if (IsEnglishPrefixLink(absUrl))
			{
				if (!ContainsColon(absUrl))
				{
					if (!IsMainPageLink(absUrl))
					{
						LinkNode newNode = new LinkNode(absUrl, node.getDepth() + 1);
						
						if ((!frontier.contains(newNode)) && (!seen.contains(newNode)))
						{
							frontier.add(newNode);
						}
					}
				}
			}
		}
	}

	public static Boolean ContainsKeyPhrase(String link) throws IOException
	{
		Document doc = Jsoup.connect(link).get();
		Elements elements = doc.getElementsContainingOwnText(keyPhrase);
		
		for (Element element: elements)
		{
			if (IsURL(element.ownText()))
			{
				continue;
			}
			else
			{
				return true;
			}
		}
		
		return false;
	}
	
	public static Boolean IsURL(String ownText)
	{
		try
		{
			URL u = new URL(ownText);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}

	public static Boolean IsEnglishPrefixLink(String link)
	{
		return link.toLowerCase().contains(prefix.toLowerCase()); 
	}
	
	public static Boolean ContainsColon(String link)
	{
		return link.substring(prefix.length()).contains(colon);
	}
	
	public static Boolean ContainsPound(String link)
	{
		return link.contains(pound);
	}
	
	public static Boolean IsMainPageLink(String link)
	{
		return link.toLowerCase().contains(mainPage.toLowerCase());
	}
}