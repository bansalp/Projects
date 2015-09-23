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
	static LinkedList<LinkNode> result = new LinkedList<LinkNode>();
	
	static String seedPage = "";
	static String keyPhrase = "";
	
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
			if (args.length == 0)
			{
				throw new Exception();
			}
			else if (args.length == 1)
			{
				URL u = new URL(args[0]);
				seedPage = args[0];
			}
			else if (args.length == 2)
			{
				URL u = new URL(args[0]);
				seedPage = args[0];
				keyPhrase = args[1];
			}
		}
		catch(Exception e)
		{
			System.out.println("Error: Illegal arguments");
			System.exit(0);
		}
		
		LinkNode seedNode = new LinkNode(seedPage, seedDepth);
		frontier.add(seedNode);
		
		while (true)
		{
			try 
			{
				crawl();
				break;
			} 
			catch (Exception e) 
			{
				System.out.println("Error: " + e.getMessage());
			}
		}
	}
	
	public static void crawl() throws IOException, InterruptedException
	{
		while (!frontier.isEmpty())
		{
			long startTime = System.currentTimeMillis();
			
			LinkNode node = frontier.getFirst();
			
			if (node.getDepth() <= maxDepth && result.size() < threshold)
			{
				int newNodeDepth = node.getDepth() + 1;
				
				if ((!keyPhrase.equals("")) && (node.getDepth() != seedDepth))
				{
					if (ContainsKeyPhrase(node.getLink()))
					{
						if (newNodeDepth <= maxDepth)
						{
							GetDocumentLinks(node, newNodeDepth);
						}
						
						System.out.println(node.getLink());
						result.add(node);
					}
				}
				else
				{
					if (newNodeDepth <= maxDepth)
					{
						GetDocumentLinks(node, newNodeDepth);
					}
					
					System.out.println(node.getLink());
					result.add(node);
				}
				
				seen.add(node);
				frontier.removeFirst();
			}
			else
			{
				break;
			}
			
			long stopTime = System.currentTimeMillis();
			long elapsedTime = stopTime - startTime;
			
			if (elapsedTime < 1000)
			{
				Thread.sleep(1000 - elapsedTime);
			}
		}
		
		WriteURLsToFile();
	}
	
	public static void WriteURLsToFile() throws FileNotFoundException, UnsupportedEncodingException
	{
		PrintWriter writer = new PrintWriter(fileName, charSet);		
		for (LinkNode link: result)
		{
			writer.println(link.getLink());
		}
		writer.close();
	}
	
	public static void GetDocumentLinks(LinkNode node, int newNodeDepth) throws IOException, InterruptedException
	{	
		Document doc = Jsoup.connect(node.getLink()).get();
		Elements links = doc.select(parseHREFNodes);
		FilterLinks(links, node, newNodeDepth);
	}
	
	public static void FilterLinks(Elements links, LinkNode node, int newNodeDepth)
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

	public static Boolean ContainsKeyPhrase(String link) throws IOException
	{
		Document doc = Jsoup.connect(link).get();
		Elements elements = doc.getElementsContainingText(keyPhrase);
		
		if (elements.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
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
	
	public static Boolean ContainsPound(String link)
	{
		return link.contains(pound);
	}
	
	public static Boolean IsMainPageLink(String link)
	{
		return link.toLowerCase().contains(mainPage.toLowerCase());
	}
}