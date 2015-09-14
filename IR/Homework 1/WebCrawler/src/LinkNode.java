public class LinkNode 
{
	private String link;
	private int depth;
	
	public LinkNode(String link, int depth)
	{
		this.link = link;
		this.depth = depth;
	}
	
	public String getLink() 
	{
		return link;
	}
	
	public int getDepth() 
	{
		return depth;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LinkNode other = (LinkNode) obj;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equalsIgnoreCase(other.link))
			return false;
		return true;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		return result;
	}
}