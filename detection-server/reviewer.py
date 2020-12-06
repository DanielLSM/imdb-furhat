from imdb import IMDb

# create an instance of the IMDb class
ia = IMDb()


class Reviwer:
    def __init__(self):
        self.ib = IMDb()
        self.limit_review = 300

    def get_all_movies_objs(self, movie_name):
        assert isinstance(movie_name, str)
        movies_objs = self.ib.search_movie(movie_name.lower())
        movies_list = [{'id':movie.getID(), 'name': movie.get('title')} for movie in movies_objs[:3]]
        return movies_list

    def get_first_id(self, movies_objs):
        return movies_objs[0].getID()

    def get_reviews_from_id(self, movie_id):
        assert isinstance(movie_id, str)
        reviews = self.ib.get_movie_reviews(movie_id)
        review_data = reviews['data']['reviews']
        return review_data

    def get_first_review(self, review_data):
        rev_content = review_data[0]['content']
        if len(rev_content) > self.limit_review:
            rev_content = rev_content[0:self.limit_review]
        return rev_content


if __name__ == "__main__":
    rev = Reviwer()
    movies_objs = rev.get_all_movies_objs("Matrix")
    movie_id = rev.get_first_id(movies_objs)
    reviews = rev.get_reviews_from_id(movie_id)
    random_review = rev.get_first_review(reviews)
