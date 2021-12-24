package jpwhitemn.docker.model;

public class DockerHubResult {

	private String name;
	private String description;
	private boolean archived;
	private long stars;
	private long pullCount;

	public DockerHubResult(String name, String description, long stars, long pullCount) {
		super();
		this.name = name;
		this.description = description;
		this.stars = stars;
		this.pullCount = pullCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public long getStars() {
		return stars;
	}

	public void setStars(long stars) {
		this.stars = stars;
	}

	public long getPullCount() {
		return pullCount;
	}

	public void setPullCount(long pullCount) {
		this.pullCount = pullCount;
	}

}
