package com.bangbangbwa.backend.domain.sns.repository;

import com.bangbangbwa.backend.domain.member.common.entity.Follow;
import com.bangbangbwa.backend.domain.member.common.entity.Member;
import com.bangbangbwa.backend.domain.member.repository.FollowRepository;
import com.bangbangbwa.backend.domain.member.repository.MemberRepository;
import com.bangbangbwa.backend.domain.oauth.common.dto.OAuthInfoDto;
import com.bangbangbwa.backend.domain.oauth.common.enums.SnsType;
import com.bangbangbwa.backend.domain.promotion.common.entity.Streamer;
import com.bangbangbwa.backend.domain.promotion.repository.StreamerRepository;
import com.bangbangbwa.backend.domain.sns.common.dto.GetLatestPostsDto;
import com.bangbangbwa.backend.domain.sns.common.entity.Post;
import com.bangbangbwa.backend.domain.sns.common.enums.PostType;
import com.bangbangbwa.backend.domain.streamer.repository.StreamerTagRepository;
import com.bangbangbwa.backend.domain.tag.common.entity.Tag;
import com.bangbangbwa.backend.domain.tag.repository.MemberTagRepository;
import com.bangbangbwa.backend.domain.tag.repository.TagRepository;
import com.bangbangbwa.backend.global.test.MyBatisTest;
import com.bangbangbwa.backend.global.util.randomValue.RandomValue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@Import({
        PostRepository.class,
        MemberRepository.class,
        StreamerRepository.class,
        StreamerTagRepository.class,
        MemberTagRepository.class,
        TagRepository.class,
        FollowRepository.class,
})
class PostRepositoryTest extends MyBatisTest {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private StreamerRepository streamerRepository;

    @Autowired
    private StreamerTagRepository streamerTagRepository;

    @Autowired
    private MemberTagRepository memberTagRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private FollowRepository followRepository;


    private Member getMember() {
        Member member = Member.builder()
                .nickname(RandomValue.string(255).setNullable(false).get())
                .build();
        OAuthInfoDto oAuthInfo = OAuthInfoDto.builder()
                .snsId(RandomValue.string(255).setNullable(false).get())
                .email(RandomValue.string(255).setNullable(false).getEmail())
                .snsType(RandomValue.getRandomEnum(SnsType.class))
                .build();
        member.addOAuthInfo(oAuthInfo);
        member.updateProfile(RandomValue.string(255).get());

        return member;
    }

    private Member createMember() {
        Member member = getMember();
        memberRepository.save(member);
        return member;
    }

    private Post getPost(PostType postType, Member member) {
        return Post.builder()
                .postType(postType)
                .memberId(member.getId())
                .title(RandomValue.string(100).setNullable(false).get())
                .content(RandomValue.string(2000).setNullable(false).get())
                .build();
    }

    private Post createPost(PostType postType, Member writeMember) {
        Post post = getPost(postType, writeMember);
        postRepository.save(post);
        return post;
    }

    private Streamer getStreamer(Member member) {
        return Streamer.builder().memberId(member.getId()).build();
    }

    private Streamer createStreamer(Member member) {
        Streamer streamer = getStreamer(member);
        streamerRepository.save(streamer);
        return streamer;
    }

    private Tag getTag() {
        return Tag.builder()
                .name(RandomValue.string(1,10).setNullable(false).get())
                .createdId("id")
                .build();
    }

    private Tag createTag() {
        Tag tag = getTag();
        tagRepository.save(tag);
        return tag;
    }

    @Test()
    void findPostsWithinLast24Hours() {
        // given
        Member writeMember = createMember();
        PostType postType = RandomValue.getRandomEnum(PostType.class);

        int readPostCount = RandomValue.getInt(0, 5);
        int newPostCount = RandomValue.getInt(0, 5);

        Set<String> readerPostList = IntStream.range(0, readPostCount)
                .mapToObj(i -> createPost(postType, writeMember).getId().toString())
                .collect(Collectors.toSet());

        List<Long> newPostList = IntStream.range(0, newPostCount)
                .mapToObj(i -> createPost(postType, writeMember))
                .map(Post::getId)
                .sorted(Comparator.naturalOrder())
                .toList();

        // when
        List<GetLatestPostsDto> postList = postRepository.findPostsWithinLast24Hours(postType, readerPostList);

        // then
        for(GetLatestPostsDto post : postList) {
            List<Long> sortedPostIds = Arrays.stream(post.getPostIdList().split(","))
                    .map(Long::valueOf)
                    .sorted()
                    .toList();

            assertThat(sortedPostIds).isEqualTo(newPostList);
        }
    }

    @Test()
    void findByPostTypeAndRandomPostsExcludingReadIds() {
        // given
        Member writeMember = createMember();
        PostType postType = RandomValue.getRandomEnum(PostType.class);
        int readPostCount = RandomValue.getInt(2);
        int newPostCount = RandomValue.getInt(3);

        Set<String> readerPostList = IntStream.range(0, readPostCount)
                .mapToObj(i -> createPost(postType, writeMember).getId().toString())
                .collect(Collectors.toSet());

        List<Long> newPostList = IntStream.range(0, newPostCount)
                .mapToObj(i -> createPost(postType, writeMember))
                .map(Post::getId)
                .sorted(Comparator.naturalOrder())
                .toList();

        // when
        List<Post> posts = postRepository.findByPostTypeAndRandomPostsExcludingReadIds(
                postType,
                3,
                readerPostList
        );

        // then
        assertThat(posts.size()).isEqualTo(newPostCount);

        List<Post> sortedPosts = posts.stream().sorted(Comparator.comparing(Post::getId)).toList();
        List<Long> sortedNewPostList = newPostList.stream().sorted().toList();

        IntStream.range(0, sortedPosts.size())
                .forEach(i -> assertThat(sortedPosts.get(i).getId())
                        .isEqualTo(sortedNewPostList.get(i)));
    }

    @Test
    void findPostsByFollowedStreamerExcludingReadIds() {
        // given
        int postLimit = 3;
        int tagCount = RandomValue.getInt(1, 5);
        int readPostCount = RandomValue.getInt(2);
        int postCount = RandomValue.getInt(0,5);

        Member member = createMember();
        Tag tag = createTag();
        memberTagRepository.save(member.getId(), tag.getId());


        Member writeMember = createMember();
        Streamer streamer = createStreamer(writeMember);
        List<Long> tagIds = Collections.singletonList(tag.getId());
        IntStream.range(0, tagCount)
                .forEach(i->streamerTagRepository.save(streamer.getId(), tag));

        IntStream.range(0, postCount)
                .forEach(i->createPost(PostType.STREAMER, writeMember));

        Set<String> readerPostList = IntStream.range(0, readPostCount)
                .mapToObj(i -> createPost(PostType.STREAMER, writeMember).getId().toString())
                .collect(Collectors.toSet());


        // when
        List<Post> posts = postRepository.findPostsByFollowedStreamerExcludingReadIds(
                postLimit,
                member.getId(),
                readerPostList
        );

        // then
        assertThat(posts.size()).isEqualTo(Math.min(postCount, postLimit));
    }

    @Test()
    void findPostsByFollowStreamerExcludingReadIds() {
        // given
        int postLimit = 3;
        int returnPostCount = RandomValue.getInt(0,5);
        int postCount = RandomValue.getInt(0,5);
        int nonReturnedPostCount = RandomValue.getInt(0,5);
        PostType postType = PostType.STREAMER;

        IntStream.range(0, nonReturnedPostCount)
                .forEach(i->createPost(postType, createMember()));

        IntStream.range(0, postCount)
                .forEach(i->createPost(postType, createMember()));

        List<Long> membersId = IntStream.range(0, returnPostCount)
                .mapToObj(i -> {
                    Member writeMember = createMember();
                    Post post = createPost(PostType.STREAMER, writeMember);
                    return post.getMemberId();
                })
                .toList();

        Member member = createMember();

        membersId.forEach(id -> {
            Follow follow = Follow.builder()
                    .followerId(member.getId())
                    .followeeId(id)
                    .build();
            followRepository.save(follow);
        });


        Set<String> readerPostList = IntStream.range(0, returnPostCount)
                .mapToObj(i -> createPost(PostType.STREAMER, member).getId().toString())
                .collect(Collectors.toSet());


        //when
        List<Post> postList = postRepository.findPostsByFollowStreamerExcludingReadIds(
                postLimit,
                member.getId(),
                readerPostList
        );

        //then
        int expectedPostCount = Math.min(returnPostCount, postLimit);
        assertThat(postList.size()).isEqualTo(expectedPostCount);
        IntStream.range(0, expectedPostCount)
                .forEach(i -> assertThat(postList.get(i).getMemberId()).isEqualTo(membersId.get(i)));
    }
//    @Test()
//    void getUnreadAndFilteredPosts() throws Exception {
//
//        // given
//        Member randomMember = createMember();
//        Streamer randomStreamer = createStreamer(randomMember);
//        Member followMember = createMember();
//        Streamer followStreamer = createStreamer(followMember);
//        Member tagMember = createMember();
//        Streamer tagStreamer = createStreamer(tagMember);
//
//        List<Long> tags = IntStream.range(0, 3)
//                .mapToObj(i -> createTag())
//                .peek(tag -> streamerTagRepository.save(tagStreamer.getId(), tag))
//                .map(Tag::getId)
//                .toList();
//
//        List<Long> randomPost = IntStream.range(0, 3)
//                .mapToObj(i -> createPost(PostType.STREAMER, randomMember))
//                .map(Post::getId)
//                .toList();
//
//        List<Long> followPost = IntStream.range(0, 3)
//                .mapToObj(i -> createPost(PostType.STREAMER, followMember))
//                .map(Post::getId)
//                .toList();
//
//        List<Long> tagPost = IntStream.range(0, 3)
//                .mapToObj(i -> createPost(PostType.STREAMER, tagMember))
//                .map(Post::getId)
//                .toList();
//
//        List<Long> allPosts = Stream.of(randomPost, followPost, tagPost)
//                .flatMap(List::stream)
//                .collect(Collectors.toList());
//        Collections.shuffle(allPosts);
//        List<Long> readPosts = allPosts.subList(0, 2);
//
//        List<Post> postList = postRepository.findAllByPostType(PostType.STREAMER);
//        for (Post post : postList) {
//            System.out.println(post.getId());
//        }
//
//        // when
////        GetStreamerPostsByTypeDto reponse = postRepository.getUnreadAndFilteredPosts(tags, readPosts, 3)
////                .orElseThrow(Exception::new);
//        GetStreamerPostsByTypeDto reponse = postRepository.getUnreadAndFilteredPosts(tags, readPosts, 3)
//                .orElseThrow(Exception::new);
//        // then
//
//        System.out.println("=Post=");
////        for (GetStreamerPostsByTypeDto.Post post : reponse.getPosts()) {
////            System.out.print(post.getPostId() + " ");
////        }
//        System.out.println();
//        System.out.println("=Post=");
//
//        System.out.println("=FollowPost=");
////        for (GetStreamerPostsByTypeDto.FollowedPost post : reponse.getFollowedPosts()) {
////            System.out.print(post.postId() + " ");
////        }
////        System.out.println("=FollowPost=");
////
////        System.out.println("=TagPost=");
////        for (GetStreamerPostsByTypeDto.TagPost post : reponse.getTagPosts()) {
////            System.out.print(post.postId() + " ");
////        }
//        System.out.println("=TagPost=");
//
//
//    }

}