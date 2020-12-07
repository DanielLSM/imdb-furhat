from imdb import IMDb
import random

# create an instance of the IMDb class
ia = IMDb()


class Reviwer:
    def __init__(self):
        self.ib = IMDb()
        self.limit_review = 300
        self.top250 = None
        self._update_top250()

    def _update_top250(self):
        if self.top250 is None:
            self.top250 = self.ib.get_top250_movies()

    def pick_movie_with_taglines(self, min_taglines):
        movie_with_taglines = None
        while movie_with_taglines == None:
            movie = random.choice(self.top250)
            self.ib.update(movie, info=['main', 'taglines'])
            taglines = movie.get('taglines')
            if (taglines is not None) and (len(taglines) >= min_taglines):
                movie_with_taglines = movie
        return movie_with_taglines

    def get_all_movies_objs(self, movie_name):
        assert isinstance(movie_name, str)
        movies_objs = self.ib.search_movie_advanced(movie_name.lower(), results=50)
        return movies_objs

    def get_movies_list(self, movie_name):
        assert isinstance(movie_name, str)
        movies_list = []

        movies_objs = self.get_all_movies_objs(movie_name)
        for movie in movies_objs:
            # Only consider legit, released movies
            if (movie.get('kind') == "movie") and (movie.get('state') is None) and (movie.get('rating') is not None) and (movie.get('gross') is not None):
                movie_item = {
                    'id':movie.getID(), 
                    'name': movie.get('title'), 
                    'year': movie.get('year'),
                    'rating': movie.get('rating'),
                    'directors':  [p['name'] for p in movie['directors']],
                    'cast': [p['name'] for p in movie['cast']],
                    'seen': True
                }
                movies_list.append(movie_item)
        if len(movies_list) == 0:
            movies_list.append({
                    'id': "123", 
                    'name': movie_name, 
                    'year': "1900",
                    'rating': "0.2",
                    'directors':  ["What's his name"],
                    'cast': ["that famous actor"],
                    'seen': False
                })
        print(movies_list)
        return sorted(movies_list, key = lambda i: int(i['year']))

    def get_first_id(self, movies_objs):
        return movies_objs[0].getID()

    def get_reviews_from_id(self, movie_id):
        assert isinstance(movie_id, str)
        reviews = self.ib.get_movie_reviews(movie_id)
        review_data = []
        try:
            review_data = reviews['data']['reviews']
        except:
            pass
        return review_data

    def get_first_review(self, review_data):
        rev_content = review_data[0]['content']
        if len(rev_content) > self.limit_review:
            rev_content = rev_content[0:self.limit_review]
        return rev_content


if __name__ == "__main__":
    rev = Reviwer()
    movies_list = rev.get_movies_list("Matrix")
    movies_objs = rev.get_all_movies_objs("Harry Potter")
    movie_id = rev.get_first_id(movies_objs)
    reviews = rev.get_reviews_from_id(movie_id)
    random_review = rev.get_first_review(reviews)

    random_movie = rev.pick_movie_with_taglines(3)
    print(f"{random_movie.get('title')}, {random_movie.get('year')}:\n{random_movie.get('taglines')}")