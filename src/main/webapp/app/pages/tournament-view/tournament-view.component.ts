import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {
  faAsterisk,
  faAt,
  faBars,
  faClock,
  faL,
  faMapMarker,
  faMoneyBill,
  faTicket,
  faUnlockAlt,
  faUser,
  faUsers,
  faWifiStrong,
} from '@fortawesome/free-solid-svg-icons';
import { Account } from 'app/core/auth/account.model';
import { AccountService } from 'app/core/auth/account.service';
import { ParticipantService } from 'app/entities/participant/service/participant.service';
import { IPlayerData } from 'app/entities/player-data/player-data.model';
import { PlayerDataService } from 'app/entities/player-data/service/player-data.service';
import { TeamService } from 'app/entities/team/service/team.service';
import { ITeam } from 'app/entities/team/team.model';
import { EntityResponseType, TournamentService } from 'app/entities/tournament/service/tournament.service';
import { ITournament } from 'app/entities/tournament/tournament.model';
import { firstValueFrom, lastValueFrom } from 'rxjs';
import { TeamPlayerService } from 'app/entities/team-player/service/team-player.service';
import { AccessStatus } from 'app/entities/enumerations/access-status.model';
import dayjs from 'dayjs/esm';
import { MatchService } from 'app/entities/match/service/match.service';
import { IMatch } from 'app/entities/match/match.model';
import { IParticipant } from 'app/entities/participant/participant.model';
import { ParticipantBracketData } from './bracket/bracket.component';
import { Breakpoints } from '@angular/cdk/layout';
import { ITeamPlayer } from 'app/entities/team-player/team-player.model';
import { ItemCountComponent } from 'app/shared/pagination/item-count.component';

export interface SignedUpTeam {
  signUpRank: number;
  teamData: ITeam;
  participantId: number;
}

export interface BracketPosition {
  bracket: number;
  X: number;
  Y: number;
}
export interface Coordinate2D {
  x: number;
  y: number;
}
export interface LineDescription {
  to: Coordinate2D;
  from: Coordinate2D;
}
export interface BracketData {
  position: BracketPosition;
  match: IMatch | undefined;
  topParticipant: ParticipantBracketData | undefined;
  bottomParticipant: ParticipantBracketData | undefined;
}
export type BracketPositions = BracketPosition[];
export type LineDescriptions = LineDescription[];
export type Brackets = BracketData[];

@Component({
  selector: 'jhi-tournament-view',
  templateUrl: './tournament-view.component.html',
  styleUrls: ['./tournament-view.component.scss'],
})
export class TournamentViewComponent implements OnInit {
  // icons
  participantCountIcon = faUsers;
  rewardIcon = faMoneyBill;
  timeIcon = faClock;
  locationIcon = faMapMarker;
  bracketFormatIcon = faBars;
  accessStatusIcon = faUnlockAlt;
  statusIcon = faWifiStrong;
  entryFeeIcon = faTicket;
  settingIcon = faAt;
  gamesPerMatchIcon = faAsterisk;
  defaultOrganiserIcon = faUser;
  defaultTeamLogo = faUsers;

  // data
  tournamentId: number = -1;
  tournamentData: ITournament | null = null;
  organiser: IPlayerData | null = null;
  signedUpTeams: SignedUpTeam[] = [];
  participantCount: number = 0;
  brackets: Brackets = [];
  lines: LineDescriptions = [];

  bracketSize = {
    width: 200, // TODO: link this to bracket scss
    height: 60,
  };
  bracketContainerStylingOverride = {};

  isFull: boolean = true;
  isTeamLeader: boolean = false;
  isParticipant: boolean = false;
  canEdit: boolean = false;
  descriptionVisible: boolean = false;
  joinSelectionVisible: boolean = false;
  account: Account | null = null;
  playerId: number | null = null;
  userParticipatingTeamId: number | null = null;
  closed: boolean = false;
  pastStartDate: boolean = true;
  matches: IMatch[] = [];
  userTeams: ITeam[] = [];
  userTeamPlayers: ITeamPlayer[] = [];
  userCanJoin: boolean = false;
  userCanLeave: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private tournamentService: TournamentService,
    private participantService: ParticipantService,
    private teamService: TeamService,
    private playerDataService: PlayerDataService,
    private accountService: AccountService,
    private teamPlayerService: TeamPlayerService,
    private matchService: MatchService
  ) {}

  // calculates the number of brackets, for the number of participants in the tournament
  bracketsPerParticipant(participants: number): number {
    if (participants < 2) {
      return participants;
    }
    return participants - 1;
  }

  // calculates the number of columns of brackets, given the number of participants
  columnsPerParticipant(participant: number): number {
    return Math.ceil(Math.log2(participant));
  }

  // calculates the column of a bracket with the given number
  columnOfBracket(bracketNumber: number): number {
    return Math.ceil(Math.log2(bracketNumber + 1));
  }

  // calculates the actual alpha position of a bracket, given its number, and some contextual information
  calculateBracketPosition(bracketNumber: number, maxNumberOfColumns: number, bracketsInLargestColumn: number): Coordinate2D {
    const columnNumberOfBracket = this.columnOfBracket(bracketNumber);

    const previousColumnBracketCount = 2 ** (columnNumberOfBracket - 1) - 1;
    const bracketOffset = 2 ** (maxNumberOfColumns - columnNumberOfBracket) - 1;
    const bracketInterval = (bracketOffset + 1) * 2;

    return {
      x: (1 + maxNumberOfColumns - columnNumberOfBracket) / (maxNumberOfColumns + 1),
      y: (bracketOffset + 1 + (bracketNumber - previousColumnBracketCount - 1) * bracketInterval) / (bracketsInLargestColumn * 2),
    };
  }

  // calculates all the X-Y coordinates of each bracket on the page, encoding them as alpha values from 0 to 1
  // places them all on an imaginary rectangular box, with the top left being 0,0 and the bottom right being 1,1
  // starts the "tree" from the middle of the right edge, i.e., 1, 0.5
  // additionally calculates the positions of all the lines between them, assuming from centre to centre
  calculateBracketPositionsAndLines(numberOfParticipants: number): { brackets: BracketPositions; lines: LineDescriptions } {
    // edge cases
    if (numberOfParticipants == 0) {
      return { brackets: [], lines: [] };
    } else if (numberOfParticipants <= 2) {
      return { brackets: [{ X: 0.5, Y: 0.5, bracket: 1 }], lines: [] };
    }

    // pre-loop calculation
    const maxNumberOfBrackets = this.bracketsPerParticipant(numberOfParticipants);
    const maxNumberOfColumns = Math.ceil(Math.log2(numberOfParticipants));
    const bracketsInLargestColumn = 2 ** (maxNumberOfColumns - 1);

    const positions: BracketPositions = [];
    const lines: LineDescriptions = [];

    for (var bracketNumber = 1; bracketNumber <= maxNumberOfBrackets; bracketNumber++) {
      const columnNumberOfBracket = this.columnOfBracket(bracketNumber);

      // calculating the bracket's position
      const currentBracketPosition = this.calculateBracketPosition(bracketNumber, maxNumberOfColumns, bracketsInLargestColumn);
      positions.push({ bracket: bracketNumber, X: currentBracketPosition.x, Y: currentBracketPosition.y });

      // calculating the positions of the line that leads to the parent bracket, if there should be one (the final bracket shouldn't have one)
      if (columnNumberOfBracket != 1) {
        const parentBracketPosition = this.calculateBracketPosition(
          Math.floor(bracketNumber / 2),
          maxNumberOfColumns,
          bracketsInLargestColumn
        );
        lines.push({
          to: {
            x: parentBracketPosition.x,
            y: parentBracketPosition.y,
          },
          from: {
            x: currentBracketPosition.x,
            y: currentBracketPosition.y,
          },
        });
      }
    }

    return { brackets: positions, lines: lines };
  }

  bracketIndexToMatchIndex(bracketIndex: number): number {
    return this.bracketsPerParticipant(this.participantCount) + 1 - bracketIndex;
  }

  regenerateBracketData() {
    const calculationResult = this.calculateBracketPositionsAndLines(this.participantCount);
    this.lines = calculationResult.lines;
    this.bracketContainerStylingOverride = {
      height: `${2 ** this.columnsPerParticipant(this.participantCount) * this.bracketSize.height}px`,
    };

    // coupling together brackets and potential matches which they might hold
    const bracketData: Brackets = [];
    for (var bracketIndex = 1; bracketIndex <= calculationResult.brackets.length; bracketIndex++) {
      // finding a match for the bracket, if one exists
      const match: IMatch | undefined = this.matches[this.bracketIndexToMatchIndex(bracketIndex)];
      let topParticipant: ParticipantBracketData | undefined = undefined;
      let bottomParticipant: ParticipantBracketData | undefined = undefined;

      if (match && match.teamOne?.id) {
        const existingParticipantData = this.signedUpTeams.find((value: SignedUpTeam) => {
          return value?.teamData?.id == match.teamOne?.id;
        });

        if (existingParticipantData?.teamData.name) {
          topParticipant = {
            name: existingParticipantData.teamData.name,
            rank: existingParticipantData.signUpRank,
          };
        }
      }

      if (match && match.teamTwo?.id) {
        const existingParticipantData = this.signedUpTeams.find((value: SignedUpTeam) => {
          return value?.teamData?.id == match.teamTwo?.id;
        });

        if (existingParticipantData?.teamData.name) {
          bottomParticipant = {
            name: existingParticipantData.teamData.name,
            rank: existingParticipantData.signUpRank,
          };
        }
      }

      bracketData.push({
        position: calculationResult.brackets[bracketIndex - 1],
        match: match,
        topParticipant: topParticipant,
        bottomParticipant: bottomParticipant,
      });
    }

    this.brackets = bracketData;
  }

  routeToEdit(): void {
    this.router.navigate(['tournament-edit/' + this.tournamentData?.id]);
  }

  canShowDescription(): boolean {
    return !!this.tournamentData?.description;
  }

  showDescriptionModal(): void {
    this.descriptionVisible = true;
  }

  hideDescriptionModal(): void {
    this.descriptionVisible = false;
  }

  showTeamJoinModal(): void {
    this.joinSelectionVisible = true;
  }

  hideTeamJoinModal(): void {
    this.joinSelectionVisible = false;
  }

  leave(): void {
    var participantId: number | null = null;
    this.signedUpTeams.forEach(participant => {
      if (participant.teamData.id == this.userParticipatingTeamId) {
        participantId = participant.participantId;
      }
    });

    if (participantId == null) {
      return;
    }

    this.participantService.leave(this.tournamentId, this.userParticipatingTeamId!, participantId).subscribe(
      async success => {
        console.log(success);
        this.isParticipant = false;
        this.userParticipatingTeamId = null;
        this.userCanJoin = this.canJoin();
        this.userCanLeave = this.canLeave();
        this.signedUpTeams = [];
        this.participantCount -= 1;
        this.brackets = [];
        this.lines = [];
        await this.getParticipants();
        await this.getMatches();
        this.regenerateBracketData();
      },
      error => {
        console.error(error);
      }
    );
  }

  signUp(selectedTeamId: number): void {
    this.participantService.join(this.tournamentId, selectedTeamId!, this.participantCount + 1).subscribe(
      async success => {
        console.log(success);
        this.isParticipant = true;
        this.userParticipatingTeamId = selectedTeamId!;
        this.userCanJoin = this.canJoin();
        this.userCanLeave = this.canLeave();
        this.signedUpTeams = [];
        this.participantCount += 1;
        this.brackets = [];
        this.lines = [];
        this.hideTeamJoinModal();
        await this.getParticipants();
        await this.getMatches();
        this.regenerateBracketData();
      },
      error => {
        console.error(error);
      }
    );
  }

  get maxParticipants() {
    if (this.tournamentData?.maxParticipants) {
      return this.tournamentData.maxParticipants;
    }
    return -1;
  }

  async setIsLeader(): Promise<void> {
    console.log('set is leader');
    try {
      const teamPlayers = await lastValueFrom(this.teamPlayerService.findLeader(this.playerId!));
      if (teamPlayers.body != null) {
        if (teamPlayers.body.length != 0) {
          this.isTeamLeader = true;
          this.userTeamPlayers = teamPlayers.body;
        }
      }
    } catch (teamPlayerError) {
      this.isTeamLeader = false;
      console.log(`Error fetching teamplayers`);
    }
  }

  canJoin(): boolean {
    const notParticipant: boolean = !this.isParticipant;
    const leaderOfATeam: boolean = this.isTeamLeader;
    const tournamentHasSpace: boolean = this.maxParticipants == -1 || this.maxParticipants > this.participantCount;
    const tournamentIsntClosed: boolean = !this.closed;
    const tournamentHasntStartedYet: boolean = !this.tournamentData?.isLive;
    const tournamentNotOver: boolean = !this.tournamentData?.ended;
    return notParticipant && leaderOfATeam && tournamentHasSpace && tournamentIsntClosed && tournamentHasntStartedYet && tournamentNotOver;
  }

  canLeave(): boolean {
    const participatesInTournament: boolean = this.isParticipant;
    const leaderOfATeam: boolean = this.isTeamLeader;
    const tournamentHasntStartedYet: boolean = !this.tournamentData?.isLive && !this.tournamentData?.ended;
    const tournamentHasntEnded: boolean = !this.tournamentData?.ended;
    return participatesInTournament && leaderOfATeam && tournamentHasntStartedYet && tournamentHasntEnded;
  }

  async setCanEdit(): Promise<void> {
    console.log('set can edit');
    const userId = (this.account as any).id;
    const data = await firstValueFrom(this.playerDataService.findPlayerDataByUserId(userId));
    if (data.body?.id) this.playerId = data.body?.id;
    this.canEdit = this.playerId == this.tournamentData?.creator?.id && !this.tournamentData?.ended;

    if (this.accountService.hasAnyAuthority('ROLE_ADMIN')) {
      this.canEdit = !this.tournamentData?.ended;
    }
  }

  async setUserData(): Promise<void> {
    if (!this.accountService.hasAnyAuthority('ROLE_USER')) {
      this.account = null;
      return;
    }
    this.account = await firstValueFrom(this.accountService.getAuthenticationState());

    if (this.account == null) return;

    const userId = (this.account as any).id;
    const data = await firstValueFrom(this.playerDataService.findPlayerDataByUserId(userId));

    if (data.body?.id) this.playerId = data.body?.id;
  }

  async setTournamentId(): Promise<void> {
    const params = await firstValueFrom(this.route.params);
    this.tournamentId = parseInt(params['id']);
  }

  async getTournament(): Promise<void> {
    try {
      const tournamentResult = await lastValueFrom(this.tournamentService.find(this.tournamentId));
      this.tournamentData = tournamentResult.body;
      this.closed = this.tournamentData?.accessStatus == AccessStatus.CLOSED;
      this.pastStartDate = this.tournamentData!.startTime!.isBefore(dayjs());
    } catch (tournamentError) {
      console.log(`Could not find tournament data for ID: ${this.tournamentId}\nError: ${tournamentError}`);
      this.router.navigate(['404']);
    }
  }

  async getMatches(): Promise<void> {
    try {
      const matchResults = await lastValueFrom(this.matchService.findAllByTournamentId(this.tournamentId));
      matchResults.body?.forEach(match => {
        if (!match.matchIndex) {
          return;
        }
        this.matches[match.matchIndex] = match;
        this.regenerateBracketData();
      });
    } catch (matchError) {
      console.log(`Could not find matches associated with tournament ${this.tournamentId}. Error:`);
      console.log(matchError);
    }
  }

  async setUserTeams(): Promise<void> {
    if (this.playerId && this.userTeamPlayers) {
      this.userTeamPlayers.forEach(async teamPlayer => {
        if (teamPlayer.team?.id) {
          try {
            const team = await lastValueFrom(this.teamService.find(teamPlayer.team?.id));
            if (team.body) {
              this.userTeams.push(team.body);
            }
          } catch (teamError) {
            console.log(teamError);
          }
        }
      });
    }
  }

  async findTeam(teamId: number, signUpRank: number, participant: IParticipant): Promise<void> {
    try {
      const teamResult = await lastValueFrom(this.teamService.find(teamId));
      if (teamResult.body) {
        this.signedUpTeams[signUpRank - 1] = {
          signUpRank: signUpRank,
          teamData: teamResult.body,
          participantId: participant.id,
        };
        this.participantCount = this.signedUpTeams.length;

        // regenerating bracket views
        this.regenerateBracketData();

        if (!this.tournamentData?.maxParticipants || this.tournamentData.maxParticipants > this.participantCount) {
          this.isFull = false;
        }
      }
    } catch (teamError) {
      console.log(
        `Could not find a team with team ID ${teamId} for the participant with participant ID ${participant.id}\nError: ${teamError}`
      );
    }
  }

  async getParticipants(): Promise<IParticipant[]> {
    try {
      const participantResults = await lastValueFrom(this.participantService.findByTournamentId(this.tournamentId));
      participantResults.body?.forEach(async participant => {
        const teamId: number | undefined = participant.team?.id;
        const signUpRank: number | null | undefined = participant.signUpRank;

        // not proceeding if a team id AND rank sign-up-rank don't exist
        if (!(teamId && signUpRank)) {
          return;
        }

        this.userTeamPlayers!.forEach(teamPlayer => {
          if (teamId == teamPlayer.team!.id) {
            this.isParticipant = true;
            this.userParticipatingTeamId = teamId;
          }
        });

        await this.findTeam(teamId, signUpRank, participant);
      });
      this.userCanJoin = this.canJoin();
      this.userCanLeave = this.canLeave();
      if (participantResults.body) return participantResults.body;
    } catch (participantError) {
      console.log(`Could not find participants of tournament with ID: ${this.tournamentId}\nError: ${participantError}`);
      this.userCanJoin = this.canJoin();
      this.userCanLeave = this.canLeave();
    }
    return [];
  }

  async findOrganiser(organiserId: number) {
    try {
      const organiserData = await lastValueFrom(this.playerDataService.find(organiserId));
      this.organiser = organiserData.body;
    } catch (organiserError) {
      console.log(`Could not find organiser with id ${organiserId}`);
    }
  }

  async ngOnInit(): Promise<void> {
    await this.setUserData();

    if (this.account != null) {
      await this.setIsLeader();
    }

    await this.setTournamentId();
    await this.getTournament();

    if (this.tournamentData?.creator) {
      const organiserId = this.tournamentData.creator.id;
      await this.findOrganiser(organiserId);
    }

    await this.getMatches();
    await this.setUserTeams();
    await this.getParticipants();

    if (this.account != null) {
      await this.setCanEdit();
    }
  }
}
